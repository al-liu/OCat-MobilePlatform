// const CompressionPlugin = require('compression-webpack-plugin')

module.exports = {
  devServer: {
    port: 8082,
    proxy: {
      '/api': {
        target: 'http://localhost:8800',
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
  // chainWebpack: config => {
  //   /* 添加分析工具 */
  //   if (process.env.NODE_ENV === 'production') {
  //     if (process.env.npm_config_report) {
  //       config
  //         .plugin('webpack-bundle-analyzer')
  //         .use(require('webpack-bundle-analyzer').BundleAnalyzerPlugin)
  //         .end()
  //       config.plugins.delete('prefetch')
  //     }
  //   }
  // },
  // configureWebpack: config => {
  //   if (process.env.NODE_ENV === 'production') {
  //     return {
  //       plugins: [
  //         new CompressionPlugin({
  //           test: /\.js$|\.html$|\.css/, // 匹配文件名
  //           threshold: 10240, // 对超过10k的数据压缩
  //           deleteOriginalAssets: false // 不删除源文件
  //         })
  //       ]
  //     }
  //   }
  // }
}
