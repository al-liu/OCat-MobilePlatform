<template>
  <div class="home">
    <van-swipe :autoplay="3000" indicator-color="white" class="advert">
      <van-swipe-item class="advertItem">1</van-swipe-item>
      <van-swipe-item class="advertItem">2</van-swipe-item>
      <van-swipe-item class="advertItem">3</van-swipe-item>
      <van-swipe-item class="advertItem">4</van-swipe-item>
    </van-swipe>
    <h1>默认首页</h1>
    <van-button type="primary" @click="toTransfer">转账</van-button>
    <p>推荐理财产品</p>
    <p>热门活动</p>
    <p>生活特惠</p>
  </div>
</template>

<script>
import Vue from 'vue'
import { Swipe, SwipeItem, Lazyload } from 'vant'
Vue.use(Swipe).use(SwipeItem).use(Lazyload)
export default {
  methods: {
    toTransfer: function () {
      this.$router.push('/transfer')
    }
  }
}
</script>
<style scoped>
  .advert {
    width: 100%;
    height: 200px;
    background-color: cornflowerblue
  }
  .advertItem {
    text-align: center;
    color: white
  }
</style>
