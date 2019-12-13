//
//  WebViewController.m
//  ios-demo
//
//  Created by 刘海川 on 2019/12/13.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "WebViewController.h"
#import <WebKit/WebKit.h>

@interface WebViewController ()

@property (nonatomic, strong) WKWebView *webView;

@end

@implementation WebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = self.titleString ? self.titleString : @"demo";
    CGRect rect = self.view.bounds;
    WKWebViewConfiguration * configuration = [[WKWebViewConfiguration alloc]init];
    _webView = [[WKWebView alloc] initWithFrame:rect configuration:configuration];
    [self.view addSubview:_webView];
    
    NSURLRequest *request = [[NSURLRequest alloc] initWithURL:self.url];
    [_webView loadRequest:request];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
