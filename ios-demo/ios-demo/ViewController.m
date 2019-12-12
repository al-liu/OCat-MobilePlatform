//
//  ViewController.m
//  ios-demo
//
//  Created by 刘海川 on 2019/11/4.
//  Copyright © 2019 lhc. All rights reserved.
//

#import "ViewController.h"
#import <WebKit/WebKit.h>

static NSString *const kRL = @"http://localhost:8866";
@interface ViewController ()

@property (nonatomic, strong) WKWebView *webView;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    CGRect rect = self.view.bounds;
    WKWebViewConfiguration * configuration = [[WKWebViewConfiguration alloc]init];
    _webView = [[WKWebView alloc] initWithFrame:rect configuration:configuration];
    _webView.backgroundColor = [UIColor orangeColor];
    [self.view addSubview:_webView];
    
    NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:kRL]];
    [_webView loadRequest:request];
}


@end
