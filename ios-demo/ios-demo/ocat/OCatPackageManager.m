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

static NSString *const OCatErrorDomain = @"OCatErrorDomain";

#if DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelVerbose;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelInfo;
#endif

@interface OCatPackageManager () <NSURLSessionDownloadDelegate> {
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

@property (nonatomic, copy)  void(^downloadCompletionHandler) (NSURL * _Nullable location);
@property (nonatomic, assign) NSUInteger downloadTaskIdentifier;
@end

@implementation OCatPackageManager

static OCatPackageManager *_instance = nil;
+ (instancetype)manageWithConfiguration:(OCatConfiguration *)configuration {
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
        NSDictionary *userInfo = @{
          NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器启动失败", nil),
          NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"离线包管理器没有配置类无法使用", nil),
          NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"使用 manageWithConfiguration 方法初始化管理器", nil)
                                  };
        NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                             code:-7
                                         userInfo:userInfo];
        [self ocat_delegateDidFailLaunchingWithError:error];
        return;
    }
    DDLogInfo(@"🚀启动离线包管理");
    DDLogVerbose(@"当前设备的 Document 沙盒目录:%@", _sandboxDocumentPath);
    if (_activePackageVersion) {
        NSString *sandboxActiveVersionPath = [_sandboxWebPath stringByAppendingPathComponent:_activePackageVersion];
        BOOL versionPathExist = [_fileManager fileExistsAtPath:sandboxActiveVersionPath];
        if (versionPathExist) {
            NSError *webServerError;
            [self ocat_startWebServer:sandboxActiveVersionPath withError:&webServerError];
            if (!webServerError) {
                [self ocat_delegateDidFinishLaunching];
            }
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
        NSDictionary *userInfo = @{
          NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器启动失败", nil),
          NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"离线包管理器没有配置类无法使用", nil),
          NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"使用 manageWithConfiguration 方法初始化管理器", nil)
                                  };
        NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                             code:-7
                                         userInfo:userInfo];
        [self ocat_delegateDidFailLaunchingWithError:error];
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
        [self ocat_delegateDidFailLaunchingWithError:createWebPathError];
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
            [self ocat_delegateDidFailLaunchingWithError:copyError];
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
                    NSDictionary *userInfo = @{
                      NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器启动失败", nil),
                      NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"解压后的预置包文件夹名称与指定的预置版本号不符", nil),
                      NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"请将预置包文件夹按照预置版本号命名后，压缩成 all.zip 包放到 pre-package 目录再重试。", nil)
                                              };
                    NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                                         code:-1
                                                     userInfo:userInfo];
                    [self ocat_delegateDidFailLaunchingWithError:error];
                    return;
                }
                DDLogInfo(@"🍺使用预置包完成，开始启动离线版本服务。");
                _activePackageVersion = prePackageVersion;
                [_userDefaults setObject:_activePackageVersion
                                  forKey:kActivePackageVersionUDKey];
                [_userDefaults synchronize];
                NSError *webServerError;
                [self ocat_startWebServer:prePackageVersionPath withError:&webServerError];
                if (!webServerError) {
                    [self ocat_delegateDidFinishLaunching];
                }
            } else {
                DDLogError(@"使用预置包错误，解压缩预置包失败！");
                NSDictionary *userInfo = @{
                  NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器启动失败", nil),
                  NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"预置压缩包解压缩失败", nil),
                  NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"请按照规范内置好压缩包后再重试。", nil)
                                          };
                NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                                     code:-2
                                                 userInfo:userInfo];
                [self ocat_delegateDidFailLaunchingWithError:error];
                return;
            }
            // 删除 web 目录下的预置包
            NSError *removeError;
            [_fileManager removeItemAtPath:sanboxWebAllZipPath
                                     error:&removeError];
            if (removeError) {
                DDLogError(@"删除 web 目录下的全量预置包失败，error:%@", removeError);
                [self ocat_delegateDidFailLaunchingWithError:removeError];
            }
        }
    }
}

- (void)ocat_startWebServer:(NSString *)path withError:(NSError * __autoreleasing *)error{
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
        *error = serverStartError;
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
        [self ocat_delegateDidFailUpdateWithError:paramsError];
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
                    [self ocat_delegateDidFailUpdateWithError:serializationError];
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
                            NSDictionary *userInfo = @{
                              NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器更新失败", nil),
                              NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"当前已是最新版本，无需更新。", nil),
                              NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"无需更新", nil)
                                                      };
                            NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                                                 code:-5
                                                             userInfo:userInfo];
                            [self ocat_delegateDidFailUpdateWithError:error];
                        }
                    } else {
                        NSString *resultMessage = responseObject[@"message"];
                        DDLogError(@"更新补丁包请求失败, message:%@", resultMessage);
                        NSString *reason = [NSString stringWithFormat:@"检查更新接口报错，%@", resultMessage];
                        NSDictionary *userInfo = @{
                          NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器更新失败", nil),
                          NSLocalizedFailureReasonErrorKey: NSLocalizedString(reason, nil),
                          NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"请重试", nil)
                                                  };
                        NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                                             code:-4
                                                         userInfo:userInfo];
                        [self ocat_delegateDidFailUpdateWithError:error];
                    }
                }
            } else {
                DDLogError(@"更新补丁包请求失败，error:%@", error);
                [self ocat_delegateDidFailUpdateWithError:error];
            }
        }];
        [dataTask resume];
    } else {
        DDLogError(@"更新补丁包失败，配置 serverBaseUrl 不合法！");
        NSDictionary *userInfo = @{
          NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器更新失败", nil),
          NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"配置类的 serverBaseUrl 不合法", nil),
          NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"请检查 serverBaseUrl 后重试", nil)
                                  };
        NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                             code:-3
                                         userInfo:userInfo];
        [self ocat_delegateDidFailUpdateWithError:error];
    }
}

- (void)ocat_downloadPathResource:(NSString *)url
                 completionHandler:(void (^)(NSURL * _Nullable location))completionHandler {
    DDLogInfo(@"开始下载补丁包...");
    NSURL *downloadUrl = [NSURL URLWithString:url];
    
    NSURLSession *urlSession = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:self delegateQueue:[NSOperationQueue mainQueue]];
    /*
    NSURLSessionDownloadTask *downloadTask = [urlSession downloadTaskWithURL:downloadUrl
                                                           completionHandler:^(NSURL * _Nullable location, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (!error) {
            DDLogInfo(@"更新补丁包下载完成。");
            completionHandler(location);
        } else {
            DDLogError(@"更新补丁包下载失败，error:%@", error);
            [self ocat_delegateDidFailUpdateWithError:error];
        }
    }];
     */
    self.downloadCompletionHandler = completionHandler;
    NSURLSessionDownloadTask *downloadTask = [urlSession downloadTaskWithURL:downloadUrl];
    self.downloadTaskIdentifier = downloadTask.taskIdentifier;
    [downloadTask resume];
}

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask
      didWriteData:(int64_t)bytesWritten
 totalBytesWritten:(int64_t)totalBytesWritten
totalBytesExpectedToWrite:(int64_t)totalBytesExpectedToWrite {
    if (downloadTask.taskIdentifier == self.downloadTaskIdentifier) {
        float progress = 1.0 * totalBytesWritten / totalBytesExpectedToWrite;
        NSLog(@"下载进度:%f", progress);
        [self ocat_delegateDownloadPatchProgress:progress];
    }
}

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask
didFinishDownloadingToURL:(NSURL *)location {
    if (downloadTask.taskIdentifier == self.downloadTaskIdentifier) {
        if (self.downloadCompletionHandler) {
            self.downloadCompletionHandler(location);
        }
    }
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    if (error) {
        if (task.taskIdentifier == self.downloadTaskIdentifier) {
            DDLogError(@"更新补丁包下载失败，error:%@", error);
            [self ocat_delegateDidFailUpdateWithError:error];
        }
    }
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
        [self ocat_delegateDidFailUpdateWithError:createPathError];
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
            [self ocat_delegateDidFailUpdateWithError:moveItemError];
            return;
        }
        // 解压缩下载补丁包
        BOOL unzipResult = [SSZipArchive unzipFileAtPath:patchFilePath
                                           toDestination:self->_sandboxPatchPath];
        if (unzipResult) {
            // 解压缩下载补丁包成功
        } else {
            DDLogError(@"补丁包合并失败，解压缩下载的补丁包失败!");
            NSDictionary *userInfo = @{
              NSLocalizedDescriptionKey: NSLocalizedString(@"包管理器更新失败", nil),
              NSLocalizedFailureReasonErrorKey: NSLocalizedString(@"下载后的补丁包解压失败", nil),
              NSLocalizedRecoverySuggestionErrorKey: NSLocalizedString(@"请检查后台下发的补丁包是否能正常解压缩", nil)
                                      };
            NSError *error = [NSError errorWithDomain:OCatErrorDomain
                                                 code:-6
                                             userInfo:userInfo];
            [self ocat_delegateDidFailUpdateWithError:error];
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
                [self ocat_delegateDidFailUpdateWithError:removeItemError];
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
                [self ocat_delegateDidFailUpdateWithError:copyItemError];
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
            NSError *webServerError;
            [self ocat_startWebServer:baseDescPath withError:&webServerError];
            if (!webServerError) {
                [self ocat_delegateDidFinishUpdate];
            }
            NSLog(@"🔥新服务启动完成");
        });
    }
}

- (void)ocat_delegateDidFinishLaunching {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerDidFinishLaunching:)]) {
        [_delegate packageManagerDidFinishLaunching:self];
    }
}

- (void)ocat_delegateDidFailLaunchingWithError:(NSError *)error {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerDidFailLaunching:withError:)]) {
        [_delegate packageManagerDidFailLaunching:self withError:error];
    }
}

- (void)ocat_delegateDidFinishUpdate {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerDidFinishUpdate:)]) {
        [_delegate packageManagerDidFinishUpdate:self];
    }
}

- (void)ocat_delegateDidFailUpdateWithError:(NSError *)error {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerDidFailUpdate:withError:)]) {
        [_delegate packageManagerDidFailUpdate:self withError:error];
    }
}

- (void)ocat_delegateDownloadPatchProgress:(float)progress {
    if (_delegate && [_delegate respondsToSelector:@selector(packageManagerDownloadPatchProgress:)]) {
        [_delegate packageManagerDownloadPatchProgress:progress];
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
