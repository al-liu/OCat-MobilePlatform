//
//  AppDelegate.m
//  ios-demo
//
//  Created by 刘海川 on 2019/11/4.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "AppDelegate.h"
#import "GCDWebServer.h"
#import "OCatPackageManager.h"

@interface AppDelegate () <OCatPackageManagerDelegate>{
    GCDWebServer *_webServer;
}

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    OCatPackageManager *packageManager = [OCatPackageManager defaultManager];
    packageManager.delegate = self;
    [packageManager startup:@"1.0.0"];

    return YES;
}
#pragma mark - OCatPackageManagerDelegate
- (NSURL *)packageManagerServerBaseUrl {
    return [NSURL URLWithString:@"http://localhost:8800"];
}
- (NSString *)packageManagerAppId {
    return @"7385262242";
}
- (NSString *)packageManagerAppSecret {
    return @"1034722ea56c4198840d1ccf77de4cab";
}

#pragma mark - UISceneSession lifecycle


- (UISceneConfiguration *)application:(UIApplication *)application configurationForConnectingSceneSession:(UISceneSession *)connectingSceneSession options:(UISceneConnectionOptions *)options {
    // Called when a new scene session is being created.
    // Use this method to select a configuration to create the new scene with.
    return [[UISceneConfiguration alloc] initWithName:@"Default Configuration" sessionRole:connectingSceneSession.role];
}


- (void)application:(UIApplication *)application didDiscardSceneSessions:(NSSet<UISceneSession *> *)sceneSessions {
    // Called when the user discards a scene session.
    // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
    // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
}


@end
