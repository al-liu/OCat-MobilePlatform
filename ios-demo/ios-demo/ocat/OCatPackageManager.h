//
//  OCatPackageManager.h
//  ios-demo
//
//  Created by 刘海川 on 2019/11/5.
//  Copyright © 2019 lhc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OCatConfiguration.h"

NS_ASSUME_NONNULL_BEGIN

/**
 如果初始化 OCatPackageManager 没有提供 OCatConfiguration 配置类，
 那么 launch 和 updateLatestPatch 方法的调用都会报错。
 */
@interface OCatPackageManager : NSObject

/// 离线包管理配置类
@property (nonatomic, readonly, strong) OCatConfiguration *configuration;

/// 当前离线包版本
@property (nonatomic, readonly, copy) NSString *activePackageVersion;

/// 离线版本访问地址
@property (nonatomic, readonly, copy) NSString *offlinePackageServer;

/// 单例初始化方法
/// @param configuration 配置类
+ (instancetype)initialization:(OCatConfiguration *)configuration;

/// 共享实例
+ (instancetype)sharedInstance;

/// 启动包管理（如果本地没有离线版本则启用内置包，并启动离线访问服务）
- (void)launch;

/// 更新补丁包到最新离线版本
- (void)updateLatestPatch;

@end

NS_ASSUME_NONNULL_END
