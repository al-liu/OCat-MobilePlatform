//
//  WebViewController.h
//  ios-demo
//
//  Created by 刘海川 on 2019/12/13.
//  Copyright © 2019 lhc. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface WebViewController : UIViewController

@property (nonatomic, strong) NSURL *url;

@property (nonatomic, copy) NSString *titleString;

@end

NS_ASSUME_NONNULL_END
