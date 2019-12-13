//
//  AppDelegate.m
//  ios-demo
//
//  Created by 刘海川 on 2019/11/4.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "AppDelegate.h"
#import "OCatPackageManager.h"
#import <CocoaLumberjack/CocoaLumberjack.h>

@interface AppDelegate () {
    
}

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    [self setupLog];
    OCatConfiguration *configuration = [OCatConfiguration initWithAppId:@"8405288128"
                                                              appSecret:@"c9663f262eb24fe3a859f0113c98efca"
                                                          serverBaseUrl:@"http://localhost:8800"
                                                  inbuiltPackageVersion:@"1.0.0"];
    OCatPackageManager *packageManager = [OCatPackageManager initialization:configuration];
    [packageManager launch];

    return YES;
}

- (void)setupLog {
    [DDLog addLogger:[DDTTYLogger sharedInstance]]; // TTY = Xcode console
    DDFileLogger *fileLogger = [[DDFileLogger alloc] init]; // File Logger
    fileLogger.rollingFrequency = 60 * 60 * 24; // 24 hour rolling
    fileLogger.logFileManager.maximumNumberOfLogFiles = 7;
    [DDLog addLogger:fileLogger];
}

#pragma mark - UISceneSession lifecycle


- (UISceneConfiguration *)application:(UIApplication *)application configurationForConnectingSceneSession:(UISceneSession *)connectingSceneSession options:(UISceneConnectionOptions *)options  API_AVAILABLE(ios(13.0)){
    // Called when a new scene session is being created.
    // Use this method to select a configuration to create the new scene with.
    return [[UISceneConfiguration alloc] initWithName:@"Default Configuration" sessionRole:connectingSceneSession.role];
}


- (void)application:(UIApplication *)application didDiscardSceneSessions:(NSSet<UISceneSession *> *)sceneSessions  API_AVAILABLE(ios(13.0)){
    // Called when the user discards a scene session.
    // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
    // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
}


@end
