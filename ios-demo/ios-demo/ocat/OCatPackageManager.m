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

static NSString *const kWebAppRootPath = @"www";
static NSString *const kWebAppPatchPath = @"patch";

static NSString *const kAllPrePackageName = @"all.zip";
static NSString *const kAllPrePackagePath = @"pre-package";
static NSString *const kZipSuffix = @".zip";
static NSString *const kActivePackageVersionUDKey = @"ACTIVE_PACKAGE_VERSION";

@interface OCatPackageManager () {
    GCDWebServer *_webServer;
    
    NSString *_sandboxDocumentPath;
    NSString *_sandboxWebPath;
    NSString *_sandboxPatchPath;
    
    NSString *_activePackageVersion;
    
    NSFileManager *_fileManager;
    NSUserDefaults *_userDefaults;
}

@end

@implementation OCatPackageManager

static OCatPackageManager *_instance = nil;
+ (instancetype)defaultManager
{
    if (_instance == nil) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            _instance = [[OCatPackageManager alloc] init];
        });
    }
    return _instance;
}
// TODO: 要不要做 appKey 和 appSecret 认证
- (instancetype)init
{
    self = [super init];
    if (self) {
        // WEB 服务初始化
        _webServer = [[GCDWebServer alloc] init];
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

- (void)startup:(NSString *)prePackageVersion {
    NSLog(@"🚀开始启动差量包管理");
    
    NSLog(@"该设备的 Document 沙盒目录:%@", _sandboxDocumentPath);
    
    // 以 _activePackageVersion 当前激活版本为主，如果是 nil，则认为是新下载客户端，使用预置包。
    if (_activePackageVersion) {
        NSString *sandboxActiveVersionPath = [_sandboxWebPath stringByAppendingPathComponent:_activePackageVersion];
        BOOL versionPathExist = [_fileManager fileExistsAtPath:sandboxActiveVersionPath];
        if (versionPathExist) {
            [self ocat_startWebServer:sandboxActiveVersionPath];
        } else {
            // TODO://
            // 检查是否存在预置代码，如果不存在从 mainBundle 中 copy。
            // 如果存在则 webserver 直接切到预置版本。
            [_userDefaults removeObjectForKey:kActivePackageVersionUDKey];
            [_userDefaults synchronize];
            [self ocat_usePrePackage:prePackageVersion];
        }
    } else {
        [self ocat_usePrePackage:prePackageVersion];
    }
    [self ocat_checkPatchResource];
}

#pragma mark - private methods
/// 根据预置版本号，使用预置压缩包的资源。
/// @param prePackageVersion 预置包版本好，预置包要与预置版本号匹配。
- (void)ocat_usePrePackage:(NSString *)prePackageVersion {
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
        NSLog(@"❌包管理中断，创建 webPath 目录失败，error:%@", createWebPathError);
        return;
    }
    if (createResult) {
        NSLog(@"将预置包 copy 到 ./www 目录下");
        NSString *prePackageComponent = [NSString stringWithFormat:@"/%@/%@", kAllPrePackagePath, kAllPrePackageName];
        NSString *prePackageBundlePath = [[NSBundle mainBundle] pathForResource:prePackageComponent ofType:@""];
        NSString *sanboxWebAllZipPath = [_sandboxWebPath stringByAppendingPathComponent:kAllPrePackageName];
        NSError *copyError;
        BOOL copyResult = [_fileManager copyItemAtPath:prePackageBundlePath
                                                toPath:sanboxWebAllZipPath
                                                 error:&copyError];
        if (copyError) {
            NSLog(@"❌包管理中断，复制预置包到 webPath 错误:%@", copyError);
            return;
        }
        if (copyResult) {
            NSLog(@"🍺 预置的全量包已经复制到 ./www 目录下。");
            NSLog(@"开始解压预置包 all.zip");
            BOOL unzipResult = [SSZipArchive unzipFileAtPath:sanboxWebAllZipPath
                                               toDestination:_sandboxWebPath];
            if (unzipResult) {
                // 校验，预置版本包和预置版本号是否一致
                NSString *prePackageVersionPath = [_sandboxWebPath stringByAppendingPathComponent:prePackageVersion];
                BOOL prePackageVersionPathExist = [_fileManager fileExistsAtPath:prePackageVersionPath];
                if (!prePackageVersionPathExist) {
                    NSLog(@"❌包管理中断，指定的预置包版本号与预置的压缩包版本不符合！");
                    return;
                }
                NSLog(@"🍺预置包解押成功，开始启动 webserver。");
                _activePackageVersion = prePackageVersion;
                [_userDefaults setObject:_activePackageVersion forKey:kActivePackageVersionUDKey];
                [_userDefaults synchronize];
                [self ocat_startWebServer:prePackageVersionPath];
            } else {
                NSLog(@"❌包管理中断，解压预置包错误");
                return;
            }
            // 删除全量包
            NSLog(@"删除预置压缩包 all.zip");
            NSError *removeError;
            [_fileManager removeItemAtPath:sanboxWebAllZipPath error:&removeError];
            if (removeError) {
                NSLog(@"⚠️删除预置压缩包失败");
            }
        } else {
            NSLog(@"❌包管理中断，复制预置包失败");
            return;
        }
    } else {
        NSLog(@"创建 ./www 目录失败，*研究下是否有 iCould 同步问题。*");
        return;
    }
}

- (void)ocat_startWebServer:(NSString *)path {
    // cacheAge delegate
    NSUInteger cacheAge = 6;// 10 min
    if (_delegate && [_delegate respondsToSelector:@selector(webServerCacheAge)]) {
        cacheAge = [_delegate webServerCacheAge];
    }
    [_webServer addGETHandlerForBasePath:@"/"
                           directoryPath:path
                           indexFilename:@"index.html"
                                cacheAge:cacheAge
                      allowRangeRequests:YES];
    NSError *serverStartError;
    [_webServer startWithOptions:@{GCDWebServerOption_BindToLocalhost:@YES,
                                   GCDWebServerOption_Port:@8866}
                           error:&serverStartError];
    if (serverStartError) {
        NSLog(@"❌服务启动失败");
    }
}

- (void)ocat_checkPatchResource {
    NSLog(@"🔍开始查询是否有差量补丁");
    NSError *paramsError;
    NSDictionary *params = @{@"versionName":_activePackageVersion,
                             @"appId":[self ocat_delegateAppId],
                             @"appSecret":[self ocat_delegateAppSecret]};
    NSData *paramsData = [NSJSONSerialization dataWithJSONObject:params
                                                         options:NSJSONWritingPrettyPrinted
                                                           error:&paramsError];
    NSURLSession *urlSession = [NSURLSession sharedSession];
    NSURL *baseURL = [self ocat_delegateServerBaseUrl];
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
                NSLog(@"查询补丁版本返回结果：%@", responseObject);
                NSString *resultCode = responseObject[@"code"];
                if ([@"000000" isEqualToString:resultCode]) {
                    NSLog(@"🍺检查补丁更新成功");
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
                        NSLog(@"当前就是最新版本，无补丁更新。");
                    }
                } else {
                    NSString *resultMessage = responseObject[@"message"];
                    NSLog(@"⚠️查询补丁报错:%@", resultMessage);
                }
            } else {
                NSLog(@"⚠️查询补丁失败:%@", error);
            }
        }];
        [dataTask resume];
    } else {
        NSLog(@"⚠️包管理中断，未实现代理方法 packageManagerServerBaseUrl");
        return;
    }
}

- (void)ocat_downloadPathResource:(NSString *)url
                 completionHandler:(void (^)(NSURL * _Nullable location))completionHandler {
    NSURL *downloadUrl = [NSURL URLWithString:url];
    NSURLSession *urlSession = [NSURLSession sharedSession];
    NSURLSessionDownloadTask *downloadTask = [urlSession downloadTaskWithURL:downloadUrl
                                                           completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (!error) {
            completionHandler(location);
        } else {
            NSLog(@"❌下载补丁资源失败:%@", error);
        }
    }];
    [downloadTask resume];
}

- (void)ocat_mergePath:(NSURL *)patchTempLocation
            newVersion:(NSString *)newVersion
        changeFileList:(NSArray *)changeFileList
        removeFileList:(NSArray *)removeFileList{
    NSLog(@"文件下载的位置：%@", patchTempLocation);
    NSLog(@"🍺下载补丁资源成功");
    
    NSString *patchFileComponent = [NSString stringWithFormat:@"%@_%@", newVersion, self->_activePackageVersion];
    NSString *patchFileName = [NSString stringWithFormat:@"%@%@", patchFileComponent, kZipSuffix];//eg 1.0.1_1.0.0.zip

    BOOL patchPathExist = [self->_fileManager fileExistsAtPath:self->_sandboxPatchPath];
    NSLog(@"./patch 补丁文件夹存在，先清除污染资源。");
    if (patchPathExist) {
        [OCatPackageManager removeFiles:self->_sandboxPatchPath];
    }
    NSLog(@"重新创建 ./patch 目录");
    NSError *createPathError;
    BOOL createResult = [self->_fileManager createDirectoryAtPath:self->_sandboxPatchPath
                                      withIntermediateDirectories:YES
                                                       attributes:nil
                                                            error:&createPathError];
    if (createPathError) {
        NSLog(@"❌包管理中断，创建 ./patch 目录失败，error:%@", createPathError);
        return;
    }
    if (createResult) {
        NSLog(@"将下载的包移至 ./patch");
        NSString *patchFilePath = [self->_sandboxPatchPath stringByAppendingPathComponent:patchFileName];
        NSURL *destination = [NSURL fileURLWithPath:patchFilePath];
        NSError *moveItemError;
        [self->_fileManager moveItemAtURL:patchTempLocation toURL:destination error:&moveItemError];
        if (moveItemError) {
            NSLog(@"❌包管理中断，移动临时目录中的补丁资源到 ./patch 失败。");
            return;
        }
        
        // 解押补丁包
        NSLog(@"解压补丁包");
        BOOL unzipResult = [SSZipArchive unzipFileAtPath:patchFilePath
                                           toDestination:self->_sandboxPatchPath];
        if (unzipResult) {
            NSLog(@"解压补丁包成功");
        } else {
            NSLog(@"❌解压补丁失败");
            return;
        }
        // 用新版本号创建 ./www/1.0.2/* 目录
        NSLog(@"在 ./www 目录，用最新版本号创建目录，如：./www/1.0.2，并将旧版本的代码移至新版本的目录中。");
        NSString *fromPath = [self->_sandboxWebPath stringByAppendingPathComponent:self->_activePackageVersion];
        NSString *toPath = [self->_sandboxWebPath stringByAppendingPathComponent:newVersion];
        if (![self->_fileManager fileExistsAtPath:toPath]) {
            [self->_fileManager createDirectoryAtPath:toPath withIntermediateDirectories:YES attributes:nil error:nil];
        }
        [OCatPackageManager copyFiles:fromPath toPath:toPath];
        
        // 开始 Merge
        // ./patch/1.0.0_1.0.2
        NSString *baseTargetPath = [self->_sandboxPatchPath stringByAppendingPathComponent:patchFileComponent];
        // ./www/1.0.2
        NSString *baseDescPath = [self->_sandboxWebPath stringByAppendingPathComponent:newVersion];
        // 修改资源与删除资源列表

        NSLog(@"按照更新补丁的删除规则，将新版本目录中的文件进行删除。");
        // 用删除清单删除 ./www/1.0.2/* 目录中的文件
        [removeFileList enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *filePath = obj;
            NSString *filePathComponent = [filePath stringByReplacingOccurrencesOfString:@"./" withString:@""];
            NSString *targetPath = [baseDescPath stringByAppendingPathComponent:filePathComponent];
            NSError *removeItemError;
            [self->_fileManager removeItemAtPath:targetPath error:&removeItemError];
            if (removeItemError) {
                NSLog(@"补丁合并，删除旧资源错误:%@", removeItemError);
                return ;
            }
        }];
        NSLog(@"补丁合并，删除旧资源成功。");
        // 用修改清单 copy 文件到 ./www/1.0.2/* 目录中
        [changeFileList enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSString *filePath = obj;
            NSString *filePathComponent = [filePath stringByReplacingOccurrencesOfString:@"./" withString:@""];
            NSString *targetPath = [baseTargetPath stringByAppendingPathComponent:filePathComponent];
            NSString *descPath = [baseDescPath stringByAppendingPathComponent:filePathComponent];
            NSError *copyItemError;
            [self->_fileManager copyItemAtPath:targetPath toPath:descPath error:&copyItemError];
            if (copyItemError) {
                NSLog(@"补丁合并，复制新资源到新版本目录时，错误:%@", copyItemError);
                return ;
            }
        }];
        NSLog(@"🍺补丁合并完成，重启 web服务。");
        // 更新版本
        self->_activePackageVersion = newVersion;
        [self->_userDefaults setObject:self->_activePackageVersion forKey:kActivePackageVersionUDKey];
        [self->_userDefaults synchronize];
        
        [OCatPackageManager removeFiles:self->_sandboxPatchPath];
        [self->_webServer stop];
        [self->_webServer removeAllHandlers];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self ocat_startWebServer:baseDescPath];
            NSLog(@"🔥新服务启动完成");
        });
    }
}

#pragma mark - access delegate methods
- (NSURL *)ocat_delegateServerBaseUrl {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerServerBaseUrl)]) {
        return [_delegate packageManagerServerBaseUrl];
    }
    return nil;
}

- (NSString *)ocat_delegateAppId {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerAppId)]) {
        return [_delegate packageManagerAppId];
    }
    return @"";
}

- (NSString *)ocat_delegateAppSecret {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerAppSecret)]) {
        return [_delegate packageManagerAppSecret];
    }
    return @"";
}

#pragma mark - static util methods

+ (void)copyFiles:(NSString *)fromPath toPath:(NSString *)toPath {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSArray<NSString *> *fromPathFileList = [fileManager contentsOfDirectoryAtPath:fromPath error:nil];
    [fromPathFileList enumerateObjectsUsingBlock:^(NSString * _Nonnull fileName, NSUInteger idx, BOOL * _Nonnull stop) {
        NSString *targetPath = [fromPath stringByAppendingPathComponent:fileName];
        NSString *destinationPath = [toPath stringByAppendingPathComponent:fileName];
        BOOL isFolder = NO;
        BOOL isExist = [fileManager fileExistsAtPath:targetPath isDirectory:&isFolder];
        if (isExist) {
            if (isFolder) {
                NSError *createError;
                BOOL createResult = [fileManager createDirectoryAtPath:destinationPath withIntermediateDirectories:YES attributes:nil error:&createError];
                if (createError) {
                    NSLog(@"❌包管理中断，复制文件到新目录时，创建子目录错误:%@", createError);
                    return ;
                }
                if (createResult) {
                    [OCatPackageManager copyFiles:targetPath toPath:destinationPath];
                }
            } else {
                NSError *copyItemError;
                [fileManager copyItemAtPath:targetPath toPath:destinationPath error:&copyItemError];
                if (copyItemError) {
                    NSLog(@"copy item error:%@", copyItemError);
                }
            }
        } else {
            NSLog(@"要复制的文件不存在，%@", targetPath);
        }
    }];
}

+ (void)removeFiles:(NSString *)targetPath {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isFolder = NO;
    BOOL isExist = [fileManager fileExistsAtPath:targetPath isDirectory:&isFolder];
    if (isExist) {
        if (isFolder) {
            NSArray<NSString *> *fromPathFileList = [fileManager contentsOfDirectoryAtPath:targetPath error:nil];
            [fromPathFileList enumerateObjectsUsingBlock:^(NSString * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                NSString *path = [targetPath stringByAppendingPathComponent:obj];
                [OCatPackageManager removeFiles:path];
            }];
        }
        NSError *removeError;
        [fileManager removeItemAtPath:targetPath error:&removeError];
        if (removeError) {
            NSLog(@"remove file error:%@", removeError);
        }
    }
}

@end
