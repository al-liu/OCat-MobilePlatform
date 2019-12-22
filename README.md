# OCat-MobilePlatform

目前依靠 H5 的 Hybrid App 仍然是客户端跨平台的主流方案，H5 本身具备很好的动态性，但是体验一直被人诟病，为了兼顾良好的体验性，各大厂商发展出以离线包为主的 Hybrid 方案，其中最著名的当属蚂蚁金服的 mpaas 产品。

这个项目实现了基于离线包的 Hybrid App 方案，支持文件级的差量更新，可生成多个版本的差量补丁包。项目有基于 vue 开发的后台管理端和后台服务，还有支持补丁更新的 iOS 和 Android 客户端，从前端到后端，提供了全栈式的解决方案。

用户在后台管理端创建应用，在应用下面上传每个版本的全量包，后台自动生成针对前几个旧版本的差量补丁，而客户端只需下载当前版本对应的补丁包，即可更新到最新版本。

## 使用方法
[请参考演示视频](https://www.bilibili.com/video/av80051552/)

部分截图：
![管理端界面截图](https://i.loli.net/2019/12/22/VNYBHsFvg2wM4po.jpg)
![ocat_demo4.jpg](https://i.loli.net/2019/12/22/lVC9Oq4amBvSZMr.jpg)

[后台管理端的云服务地址 http://49.233.169.151:8081](http://49.233.169.151:8081/)
账号为 demo/demo，该账号没有操作权限只有只读权限，但所有菜单可见。

## 开发环境说明

```
OCat-MobilePlatform     # 项目根目录
├─android-demo          # Android 客户端
├─database              # 数据库表及初始数据
├─deploy                # 部署到云服务
├─docker                # 开发时需要的 docker 环境
├─ios-demo              # iOS 客户端
├─mobileplatform        # 项目后台服务
├─mobileplatform-portal # 项目后管前端
├─postman               # 接口测试集合
├─test-package          # 测试的离线压缩包
└ vue-demo              # 测试的离线包前端
```

### 后台服务
首先，用 docker-compose 部署好 mysql 及 nginx 环境，执行初始化 sql 脚本。

打开 mobileplatform 项目，修改 application-dev.yml 配置文件。

如下：

```yml
com:
  lhc:
    ocat:
      mobileplatform:
        max-diff-count: 3 # 最大差量比较的版本个数
        workshop-path: /项目所在父目录/OCat-MobilePlatform/mobileplatform/workshop # 差量比较工作目录
        online-url: http://localhost:8080
        package-download-path: /项目所在父目录/OCat-MobilePlatform/docker/nginx/html/download/packages
        online-version-path: /项目所在父目录/OCat-MobilePlatform/docker/nginx/html/online
        prep-online-version-path: /项目所在父目录/OCat-MobilePlatform/docker/nginx/html/preview
```

配置文件修改完毕即可运行该项目。

### 后管前端
然后打开，mobileplatform-portal 前端项目，打开 vue.config.js 文件，配置代理转发。

```js
module.exports = {
  devServer: {
    // 前端项目的访问端口
    port: 8082, 
    proxy: {
      // 转发服务的匹配路径
      '/api': { 
        // 要转发的服务，即刚起的后台服务地址
        target: 'http://localhost:9090', 
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
}
```

打开 `./src/utils/network/config.js` 配置接口服务。

```js
export default {
  // 基础url前缀
  baseURL: 'http://localhost:8082/api',
  ...
}

```

配置好后即可在 mobileplatform-portal 根目录，使用命名 `npm run serve` 运行服务。

### 客户端
客户端以 iOS 为例，首先内置离线包，在 pre-package 目录下内置 all.zip 压缩包，该压缩包即是 1.0.0 版本的包。

然后，配置对应应用程序的 AppId，AppSecret，后台服务的地址，及内置版本号后即可使用。

```oc
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    [self setupLog];
    OCatConfiguration *configuration = [OCatConfiguration initWithAppId:@"6218890139"
                                                              appSecret:@"4a4e2497e7e14f43bbb789a128b4db01"
                                                          serverBaseUrl:@"http://localhost:9090"
                                                  inbuiltPackageVersion:@"1.0.0"];
    OCatPackageManager *packageManager = [OCatPackageManager manageWithConfiguration:configuration];
    [packageManager launch];

    return YES;
}
```


