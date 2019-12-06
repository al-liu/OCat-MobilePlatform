<template>
  <div>
      <h1>我的首页</h1>
      <van-grid :column-num="3">
        <van-grid-item icon="gift-o" text="商品" dot to="/goods" />
        <van-grid-item icon="balance-pay" text="钱包" />
        <van-grid-item icon="chat-o" text="消息" info="99+" />
      </van-grid>
      <van-button type="primary" @click="toAbout">关于</van-button>
  </div>
</template>

<script>
import Vue from 'vue'
import { Notify, Grid, GridItem } from 'vant'
Vue.use(Notify).use(Grid).use(GridItem)
export default {
  methods: {
    toAbout: function () {
      Notify({ type: 'warning', message: 'about 页面已被删除' })
    }
  }
}
</script>
