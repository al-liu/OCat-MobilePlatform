//
//  OCatConfiguration.h
//  ios-demo
//
//  Created by 刘海川 on 2019/12/12.
//  Copyright © 2019 lhc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OCatConfiguration : NSObject

@property (nonatomic, copy) NSString *appId;
@property (nonatomic, copy) NSString *appSecret;
@property (nonatomic, copy) NSString *serverBaseUrl;
@property (nonatomic, copy) NSString *inbuiltPackageVersion;

+ (instancetype)initWithAppId:(NSString *)appId
                    appSecret:(NSString *)appSecret
                serverBaseUrl:(NSString *)serverBaseUrl
        inbuiltPackageVersion:(NSString *)inbuiltPackageVersion;

@end

NS_ASSUME_NONNULL_END
