//
//  OCatPackageManager.h
//  ios-demo
//
//  Created by 刘海川 on 2019/11/5.
//  Copyright © 2019 lhc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol OCatPackageManagerDelegate <NSObject>
@required
- (NSURL *)packageManagerServerBaseUrl;
- (NSString *)packageManagerAppId;
- (NSString *)packageManagerAppSecret;
@optional
- (NSUInteger)webServerCacheAge;

@end

@interface OCatPackageManager : NSObject

@property (nonatomic, weak) id<OCatPackageManagerDelegate> delegate;

+ (instancetype)defaultManager;

- (void)startup:(NSString *)prePackageVersion;

+ (void)copyFiles:(NSString *)fromPath toPath:(NSString *)toPath;

@end

NS_ASSUME_NONNULL_END
