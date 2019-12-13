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

static NSString *const kOnlineServerUrl = @"http://localhost:8080";

@interface ViewController ()

@property (weak, nonatomic) IBOutlet UILabel *inbuiltVersionLabel;
@property (weak, nonatomic) IBOutlet UILabel *currentVersionLabel;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    OCatPackageManager *pm = [OCatPackageManager sharedInstance];
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

@end
