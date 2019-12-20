//
//  ViewController.m
//  ios-demo
//
//  Created by 刘海川 on 2019/11/4.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "ViewController.h"
#import "WebViewController.h"
#import "OCatPackageManager.h"
#import <CocoaLumberjack/CocoaLumberjack.h>
#import <WebKit/WebKit.h>
#import "SVProgressHUD.h"

static NSString *const kOnlineServerUrl = @"http://49.233.169.151:8080";

@interface ViewController () <OCatPackageManagerDelegate>

@property (weak, nonatomic) IBOutlet UILabel *inbuiltVersionLabel;
@property (weak, nonatomic) IBOutlet UILabel *currentVersionLabel;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    OCatPackageManager *pm = [OCatPackageManager sharedInstance];
    pm.delegate = self;
    self.inbuiltVersionLabel.text = [NSString stringWithFormat:@"内置版本:%@", pm.configuration.inbuiltPackageVersion];
    self.currentVersionLabel.text = [NSString stringWithFormat:@"当前版本:%@", pm.activePackageVersion];
}
- (IBAction)gotoOfflineVersion:(id)sender {
    NSURL *url = [NSURL URLWithString:[OCatPackageManager sharedInstance].offlinePackageServer];
    WebViewController *controller = [[WebViewController alloc] init];
    controller.url = url;
    [self.navigationController pushViewController:controller animated:YES];
}
- (IBAction)gotoOnlineVersion:(id)sender {
    NSURL *url = [NSURL URLWithString:kOnlineServerUrl];
    WebViewController *controller = [[WebViewController alloc] init];
    controller.url = url;
    [self.navigationController pushViewController:controller animated:YES];
}
- (IBAction)updateLatestPatch:(id)sender {
    [SVProgressHUD showWithStatus:@"开始更新离线补丁包"];
    [[OCatPackageManager sharedInstance] updateLatestPatch];
}
- (IBAction)clearWebViewCache:(id)sender {
    if (@available(iOS 9.0, *)) {
        [SVProgressHUD showWithStatus:@"清理缓存"];
        NSSet *websiteDataTypes = [WKWebsiteDataStore allWebsiteDataTypes];
        NSDate *dateFrom = [NSDate dateWithTimeIntervalSince1970:0];
        [[WKWebsiteDataStore defaultDataStore] removeDataOfTypes:websiteDataTypes modifiedSince:dateFrom completionHandler:^{
            NSLog(@"iOS9 WKWebView 清除缓存完毕！！！");
            [SVProgressHUD dismissWithCompletion:^{
                [SVProgressHUD showSuccessWithStatus:@"清理完毕"];
            }];
        }];
    } else {
        // Fallback on earlier versions
        NSLog(@"其他iOS版本清除缓存。");
    }
}

#pragma mark - delegate <OCatPackageManagerDelegate>
- (void)packageManagerDidFinishLaunching:(OCatPackageManager *)packageManager {
    NSLog(@"packageManagerDidFinishLaunching");
}

- (void)packageManagerDidFailLaunching:(OCatPackageManager *)packageManager
                             withError:(NSError *)error {
    NSLog(@"packageManagerDidFailLaunching:%@", error);
    [SVProgressHUD showErrorWithStatus:[error localizedFailureReason]];
}

- (void)packageManagerDidFinishUpdate:(OCatPackageManager *)packageManager {
    NSLog(@"packageManagerDidFinishUpdate");
    self.currentVersionLabel.text = [NSString stringWithFormat:@"当前版本:%@", packageManager.activePackageVersion];
    [SVProgressHUD dismissWithCompletion:^{
        [SVProgressHUD showSuccessWithStatus:@"更新离线补丁包完成"];
    }];
}

- (void)packageManagerDidFailUpdate:(OCatPackageManager *)packageManager
                          withError:(NSError *)error {
    NSLog(@"packageManagerDidFailUpdate:%@", error);
    [SVProgressHUD dismissWithCompletion:^{
        [SVProgressHUD showErrorWithStatus:[error localizedFailureReason]];
    }];
}

- (void)packageManagerDownloadPatchProgress:(float)progress {
    NSLog(@"packageManagerDownloadPatchProgress:%f", progress);
    [SVProgressHUD showProgress:progress status:@"正在下载离线补丁包"];
}

@end
