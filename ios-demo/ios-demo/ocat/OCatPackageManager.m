//
//  OCatPackageManager.m
//  ios-demo
//
//  Created by 刘海川 on 2019/11/5.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "OCatPackageManager.h"
#import "SSZipArchive.h"
#import "GCDWebServer.h"
#import <CocoaLumberjack/CocoaLumberjack.h>

static NSString *const kWebAppRootPath = @"www";
static NSString *const kWebAppPatchPath = @"patch";

static NSString *const kAllPrePackageName = @"all.zip";
static NSString *const kAllPrePackagePath = @"pre-package";
static NSString *const kZipSuffix = @".zip";
static NSString *const kActivePackageVersionUDKey = @"ACTIVE_PACKAGE_VERSION";

#if DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelInfo;
#endif

@interface OCatPackageManager () {
    GCDWebServer *_webServer;
    
    NSString *_sandboxDocumentPath;
    NSString *_sandboxWebPath;
    NSString *_sandboxPatchPath;
    
    NSString *_activePackageVersion;
    
    NSFileManager *_fileManager;
    NSUserDefaults *_userDefaults;
}

@property (nonatomic, readwrite, strong) OCatConfiguration *configuration;
@property (nonatomic, readwrite, copy) NSString *activePackageVersion;
@property (nonatomic, readwrite, copy) NSString *offlinePackageServer;

@end

@implementation OCatPackageManager

static OCatPackageManager *_instance = nil;
+ (instancetype)initialization:(OCatConfiguration *)configuration
{
    OCatPackageManager *pm = [OCatPackageManager sharedInstance];
    pm.configuration = configuration;
    return pm;
}

+ (instancetype)sharedInstance {
    if (_instance == nil) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            _instance = [[OCatPackageManager alloc] init];
        });
    }
    return _instance;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        // WEB 服务初始化
        _webServer = [[GCDWebServer alloc] init];
        // 常用工具对象
        _fileManager = [NSFileManager defaultManager];
        _userDefaults = [NSUserDefaults standardUserDefaults];
        // 常用路径
        _sandboxDocumentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
        _sandboxWebPath = [_sandboxDocumentPath stringByAppendingPathComponent:kWebAppRootPath];
        _sandboxPatchPath = [_sandboxDocumentPath stringByAppendingPathComponent:kWebAppPatchPath];
        // 获取可用版本
        _activePackageVersion = [_userDefaults stringForKey:kActivePackageVersionUDKey];
    }
    return self;
}

- (void)launch {
    if (self.configuration == nil) {
        DDLogError(@"离线包管理器没有配置类无法使用");
        return;
    }
    DDLogInfo(@"🚀启动离线包管理");
    DDLogVerbose(@"当前设备的 Document 沙盒目录:%@", _sandboxDocumentPath);
    if (_activePackageVersion) {
        NSString *sandboxActiveVersionPath = [_sandboxWebPath stringByAppendingPathComponent:_activePackageVersion];
        BOOL versionPathExist = [_fileManager fileExistsAtPath:sandboxActiveVersionPath];
        if (versionPathExist) {
            [self ocat_startWebServer:sandboxActiveVersionPath];
        } else {
            [_userDefaults removeObjectForKey:kActivePackageVersionUDKey];
            [_userDefaults synchronize];
            [self ocat_usePrePackage:self.configuration.inbuiltPackageVersion];
        }
    } else {
        [self ocat_usePrePackage:self.configuration.inbuiltPackageVersion];
    }
}

- (void)updateLatestPatch {
    if (self.configuration == nil) {
        DDLogError(@"离线包管理器没有配置类无法使用");
        return;
    }
    [self ocat_checkPatchResource];
}

#pragma mark - private methods
/// 根据预置版本号，使用预置压缩包的资源。
/// @param prePackageVersion 预置包版本好，预置包要与预置版本号匹配。
- (void)ocat_usePrePackage:(NSString *)prePackageVersion {
    // 创建 web 目录
    BOOL webPathExist = [_fileManager fileExistsAtPath:_sandboxWebPath];
    if (webPathExist) {
        [OCatPackageManager removeFiles:_sandboxWebPath];
    }
    NSError *createWebPathError;
    BOOL createResult = [_fileManager createDirectoryAtPath:_sandboxWebPath
                                withIntermediateDirectories:YES
                                                 attributes:nil
                                                      error:&createWebPathError];
    if (createWebPathError) {
        DDLogError(@"使用预置包错误，web 目录创建失败，error:%@", createWebPathError);
        return;
    }
    // 创建 web 目录 END
    if (createResult) {
        // 复制预置包到 web 目录，from: mainBundle://pre-package/all.zip to: sandbox://docuemnt/www/all.zip
        NSString *prePackageComponent = [NSString stringWithFormat:@"/%@/%@",
                                         kAllPrePackagePath,
                                         kAllPrePackageName];
        NSString *prePackageBundlePath = [[NSBundle mainBundle] pathForResource:prePackageComponent
                                                                         ofType:@""];
        NSString *sanboxWebAllZipPath = [_sandboxWebPath stringByAppendingPathComponent:kAllPrePackageName];
        NSError *copyError;
        BOOL copyResult = [_fileManager copyItemAtPath:prePackageBundlePath
                                                toPath:sanboxWebAllZipPath
                                                 error:&copyError];
        if (copyError) {
            DDLogError(@"使用预置包错误，复制预置包到 web 目录失败，error:%@", copyError);
            return;
        }
        if (copyResult) {
            // 解压缩复制完成的预置包
            BOOL unzipResult = [SSZipArchive unzipFileAtPath:sanboxWebAllZipPath
                                               toDestination:_sandboxWebPath];
            if (unzipResult) {
                // 校验预置版本包和预置版本号是否一致（备注：预置包 all.zip 压缩目录命名需要和预置版本号保持一致，e.g. 1.0.0/..
                NSString *prePackageVersionPath = [_sandboxWebPath stringByAppendingPathComponent:prePackageVersion];
                BOOL prePackageVersionPathExist = [_fileManager fileExistsAtPath:prePackageVersionPath];
                if (!prePackageVersionPathExist) {
                    DDLogError(@"使用预置包错误，指定的预置包版本号与预置的压缩包文件名不符！");
                    return;
                }
                DDLogInfo(@"🍺使用预置包完成，开始启动离线版本服务。");
                _activePackageVersion = prePackageVersion;
                [_userDefaults setObject:_activePackageVersion
                                  forKey:kActivePackageVersionUDKey];
                [_userDefaults synchronize];
                [self ocat_startWebServer:prePackageVersionPath];
            } else {
                DDLogError(@"使用预置包错误，解压缩预置包失败！");
                return;
            }
            // 删除 web 目录下的预置包
            NSError *removeError;
            [_fileManager removeItemAtPath:sanboxWebAllZipPath
                                     error:&removeError];
            if (removeError) {
                DDLogError(@"删除 web 目录下的全量预置包失败，error:%@", removeError);
            }
        }
    }
}

- (void)ocat_startWebServer:(NSString *)path {
    DDLogInfo(@"启动离线包 web 服务...");
    NSUInteger cacheAge = 5; // 10 min
    NSNumber *port = @8866;
    [_webServer addGETHandlerForBasePath:@"/"
                           directoryPath:path
                           indexFilename:@"index.html"
                                cacheAge:cacheAge
                      allowRangeRequests:YES];
    NSError *serverStartError;
    [_webServer startWithOptions:@{GCDWebServerOption_BindToLocalhost:@YES,
                                   GCDWebServerOption_Port:port}
                           error:&serverStartError];
    if (serverStartError) {
        DDLogInfo(@"启动离线版本服务失败，error:%@", serverStartError);
    } else {
        DDLogInfo(@"启动离线包 web 服务完成");
    }
    _offlinePackageServer = [NSString stringWithFormat:@"http://localhost:%@", port];
}

- (void)ocat_checkPatchResource {
    DDLogInfo(@"☁️开始查询最新补丁包");
    NSError *paramsError;
    NSDictionary *params = @{@"versionName":_activePackageVersion,
                             @"appId":self.configuration.appId,
                             @"appSecret":self.configuration.appSecret};
    NSData *paramsData = [NSJSONSerialization dataWithJSONObject:params
                                                         options:NSJSONWritingPrettyPrinted
                                                           error:&paramsError];
    if (paramsError) {
        DDLogError(@"更新补丁包失败，jsonObject 转换错误:%@", paramsError);
        return;
    }
    NSURLSession *urlSession = [NSURLSession sharedSession];
    NSURL *baseURL = [NSURL URLWithString:self.configuration.serverBaseUrl];
    if (baseURL) {
        NSURL *url = [NSURL URLWithString:@"/package/fetch" relativeToURL:baseURL];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
        [request setValue:@"application/json; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
        request.HTTPMethod = @"POST";
        request.HTTPBody = paramsData;
        NSURLSessionDataTask *dataTask = [urlSession dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
            if (!error) {
                NSError *serializationError;
                NSDictionary *responseObject = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&serializationError];
                if (serializationError) {
                    DDLogError(@"更新补丁包失败，response json 转换错误:%@", serializationError);
                } else {
                    DDLogInfo(@"更新补丁请求结果:%@", responseObject);
                    NSString *resultCode = responseObject[@"code"];
                    if ([@"000000" isEqualToString:resultCode]) {
                        DDLogInfo(@"🍺更新补丁包请求成功");
                        NSDictionary *data = responseObject[@"data"];
                        NSString *newVersionName = data[@"newVersion"];
                        NSString *oldVersionName = data[@"oldVersion"];
                        if (![newVersionName isEqualToString:oldVersionName]) {
                            NSString *resourceUrl = data[@"downloadUrl"];
                            NSArray *changeResources = data[@"changeResourceInfo"];
                            NSArray *removeResources = data[@"removeResourceInfo"];
                            [self ocat_downloadPathResource:resourceUrl completionHandler:^(NSURL * _Nullable location) {
                                [self ocat_mergePath:location
                                          newVersion:newVersionName
                                      changeFileList:changeResources
                                      removeFileList:removeResources];
                            }];
                        } else {
                            DDLogInfo(@"当前已是最新版本，无需更新。");
                        }
                    } else {
                        NSString *resultMessage = responseObject[@"message"];
                        DDLogError(@"更新补丁包请求失败, message:%@", resultMessage);
                    }
                }
            } else {
                DDLogError(@"更新补丁包请求失败，error:%@", error);
            }
        }];
        [dataTask resume];
    } else {
        DDLogError(@"更新补丁包失败，配置 serverBaseUrl 不合法！");
    }
}

- (void)ocat_downloadPathResource:(NSString *)url
                 completionHandler:(void (^)(NSURL * _Nullable location))completionHandler {
    DDLogInfo(@"开始下载补丁包...");
    NSURL *downloadUrl = [NSURL URLWithString:url];
    NSURLSession *urlSession = [NSURLSession sharedSession];
    NSURLSessionDownloadTask *downloadTask = [urlSession downloadTaskWithURL:downloadUrl
                                                           completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (!error) {
            DDLogInfo(@"更新补丁包下载完成。");
            completionHandler(location);
        } else {
            DDLogError(@"更新补丁包下载失败，error:%@", error);
        }
    }];
    [downloadTask resume];
}

- (void)ocat_mergePath:(NSURL *)patchTempLocation
            newVersion:(NSString *)newVersion
        changeFileList:(NSArray *)changeFileList
        removeFileList:(NSArray *)removeFileList{
    DDLogInfo(@"开始合并最新补丁包");
    NSString *patchFileComponent = [NSString stringWithFormat:@"%@_%@",
                                    newVersion,
                                    self->_activePackageVersion];
    // e.g. filename is 1.0.1_1.0.0.zip
    NSString *patchFileName = [NSString stringWithFormat:@"%@%@",
                               patchFileComponent,
                               kZipSuffix];
    // 开始创建 patch 目录
    BOOL patchPathExist = [self->_fileManager fileExistsAtPath:self->_sandboxPatchPath];
    if (patchPathExist) {
        [OCatPackageManager removeFiles:self->_sandboxPatchPath];
    }
    NSError *createPathError;
    BOOL createResult = [self->_fileManager createDirectoryAtPath:self->_sandboxPatchPath
                                      withIntermediateDirectories:YES
                                                       attributes:nil
                                                            error:&createPathError];
    if (createPathError) {
        DDLogError(@"补丁包合并失败，patch 目录创建错误，error:%@", createPathError);
        return;
    }
    // 开始创建 patch 目录 END
    if (createResult) {
        // 将下载包移至指定目录
        NSString *patchFilePath = [self->_sandboxPatchPath stringByAppendingPathComponent:patchFileName];
        NSURL *destination = [NSURL fileURLWithPath:patchFilePath];
        NSError *moveItemError;
        [self->_fileManager moveItemAtURL:patchTempLocation toURL:destination error:&moveItemError];
        if (moveItemError) {
            DDLogError(@"补丁包合并失败，移动下载包失败，error:%@", moveItemError);
            return;
        }
        // 解压缩下载补丁包
        BOOL unzipResult = [SSZipArchive unzipFileAtPath:patchFilePath
                                           toDestination:self->_sandboxPatchPath];
        if (unzipResult) {
            // 解压缩下载补丁包成功
        } else {
            DDLogError(@"补丁包合并失败，解压缩下载的补丁包失败!");
            return;
        }
        // 将 web(./www) 目录中当前版本的代码，复制一份到以新版本号命名的文件夹中一份。 e.g. 当前 1.0.0/** COPY TO 新版 1.0.1/**
        NSString *fromPath = [self->_sandboxWebPath stringByAppendingPathComponent:self->_activePackageVersion];
        NSString *toPath = [self->_sandboxWebPath stringByAppendingPathComponent:newVersion];
        if (![self->_fileManager fileExistsAtPath:toPath]) {
            [self->_fileManager createDirectoryAtPath:toPath withIntermediateDirectories:YES attributes:nil error:nil];
        }
        [OCatPackageManager copyFiles:fromPath toPath:toPath];
        DDLogVerbose(@"开始合并文件...");
        // 解压缩后的最新补丁包目录 (e.g. ./patch/1.0.0_1.0.2)
        NSString *baseTargetPath = [self->_sandboxPatchPath stringByAppendingPathComponent:patchFileComponent];
        // web 目录下的最新版本目录 (e.g. ./www/1.0.2)
        NSString *baseDescPath = [self->_sandboxWebPath stringByAppendingPathComponent:newVersion];
        // 对旧版本代码进行修改资源和删除资源的操作
        // 遵循删除文件清单进行删除（目标目录为 e.g. ./www/1.0.2/*)
        [removeFileList enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *filePath = obj;
            NSString *filePathComponent = [filePath stringByReplacingOccurrencesOfString:@"./" withString:@""];
            NSString *targetPath = [baseDescPath stringByAppendingPathComponent:filePathComponent];
            NSError *removeItemError;
            [self->_fileManager removeItemAtPath:targetPath error:&removeItemError];
            if (removeItemError) {
                DDLogError(@"合并补丁，删除旧资源时发生错误,error:%@", removeItemError);
                return ;
            }
        }];
        // 遵循变更文件清单进行变更(COPY)（目标目录为 e.g. ./www/1.0.2/*)
        [changeFileList enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *filePath = obj;
            NSString *filePathComponent = [filePath stringByReplacingOccurrencesOfString:@"./" withString:@""];
            NSString *targetPath = [baseTargetPath stringByAppendingPathComponent:filePathComponent];
            NSString *descPath = [baseDescPath stringByAppendingPathComponent:filePathComponent];
            NSError *copyItemError;
            [self->_fileManager copyItemAtPath:targetPath toPath:descPath error:&copyItemError];
            if (copyItemError) {
                DDLogError(@"合并补丁，变更旧资源为新资源时发生错误,error:%@", copyItemError);
                return ;
            }
        }];
        self->_activePackageVersion = newVersion;
        [self->_userDefaults setObject:self->_activePackageVersion forKey:kActivePackageVersionUDKey];
        [self->_userDefaults synchronize];
        DDLogInfo(@"最新补丁包合并完成，当前最新离线版本为:%@", self->_activePackageVersion);
        [OCatPackageManager removeFiles:self->_sandboxPatchPath];
        DDLogInfo(@"重启离线包 web 服务。");
        if (self->_webServer.running) {
            [self->_webServer stop];
        }
        [self->_webServer removeAllHandlers];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self ocat_startWebServer:baseDescPath];
            NSLog(@"🔥新服务启动完成");
        });
    }
}

#pragma mark - static util methods

+ (void)copyFiles:(NSString *)fromPath toPath:(NSString *)toPath {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSArray<NSString *> *fromPathFileList = [fileManager contentsOfDirectoryAtPath:fromPath
                                                                             error:nil];
    [fromPathFileList enumerateObjectsUsingBlock:^(NSString * _Nonnull fileName,
                                                   NSUInteger idx,
                                                   BOOL * _Nonnull stop) {
        NSString *targetPath = [fromPath stringByAppendingPathComponent:fileName];
        NSString *destinationPath = [toPath stringByAppendingPathComponent:fileName];
        BOOL isFolder = NO;
        BOOL isExist = [fileManager fileExistsAtPath:targetPath isDirectory:&isFolder];
        if (isExist) {
            if (isFolder) {
                NSError *createError;
                BOOL createResult = [fileManager createDirectoryAtPath:destinationPath
                                           withIntermediateDirectories:YES
                                                            attributes:nil
                                                                 error:&createError];
                if (createError) {
                    DDLogError(@"复制文件到新目录时，创建子目录发生错误:%@", createError);
                    return ;
                }
                if (createResult) {
                    [OCatPackageManager copyFiles:targetPath
                                           toPath:destinationPath];
                }
            } else {
                NSError *copyItemError;
                [fileManager copyItemAtPath:targetPath
                                     toPath:destinationPath
                                      error:&copyItemError];
                if (copyItemError) {
                    DDLogError(@"复制文件出错:%@", copyItemError);
                }
            }
        } else {
            DDLogError(@"要复制的文件不存在:%@", targetPath);
        }
    }];
}

+ (void)removeFiles:(NSString *)targetPath {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isFolder = NO;
    BOOL isExist = [fileManager fileExistsAtPath:targetPath isDirectory:&isFolder];
    if (isExist) {
        if (isFolder) {
            NSArray<NSString *> *fromPathFileList = [fileManager contentsOfDirectoryAtPath:targetPath
                                                                                     error:nil];
            [fromPathFileList enumerateObjectsUsingBlock:^(NSString * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                NSString *path = [targetPath stringByAppendingPathComponent:obj];
                [OCatPackageManager removeFiles:path];
            }];
        }
        NSError *removeError;
        [fileManager removeItemAtPath:targetPath error:&removeError];
        if (removeError) {
            DDLogError(@"删除文件错误:%@", removeError);
        }
    }
}

@end
