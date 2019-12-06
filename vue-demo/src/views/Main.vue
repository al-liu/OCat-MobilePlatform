<template>
  <div id="app">
    <van-nav-bar
      :title="title"
    />
    <router-view/>
    <van-tabbar v-model="active" @change="taptab" route>
      <van-tabbar-item
        icon="home-o"
        to="/main/home"
        replace>首页</van-tabbar-item>
      <van-tabbar-item
        icon="gold-coin-o"
        to="/main/financing"
        replace>理财</van-tabbar-item>
      <van-tabbar-item
        icon="point-gift-o"
        to="/main/life"
        replace>生活</van-tabbar-item>
      <van-tabbar-item
        icon="contact"
        to="/main/mine"
        replace>我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>

export default {
  data () {
    return {
      active: 0,
      title: '首页'
    }
  },
  methods: {
    taptab: function (active) {
      switch (active) {
        case 1:
          this.title = '理财'
          break
        case 2:
          this.title = '生活'
          break
        case 3:
          this.title = '我的'
          break
        default:
          this.title = '首页'
          break
      }
    }
  }
}
</script>

<style>
</style>
