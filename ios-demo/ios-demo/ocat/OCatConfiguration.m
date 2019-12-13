//
//  OCatConfiguration.m
//  ios-demo
//
//  Created by 刘海川 on 2019/12/12.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "OCatConfiguration.h"

@implementation OCatConfiguration

+ (instancetype)initWithAppId:(NSString *)appId
                    appSecret:(NSString *)appSecret
                serverBaseUrl:(NSString *)serverBaseUrl
        inbuiltPackageVersion:(NSString *)inbuiltPackageVersion {
    OCatConfiguration *configuration = [[OCatConfiguration alloc] init];
    configuration.appId = appId;
    configuration.appSecret = appSecret;
    configuration.serverBaseUrl = serverBaseUrl;
    configuration.inbuiltPackageVersion = inbuiltPackageVersion;
    return configuration;
}

@end
