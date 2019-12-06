<template>
  <div>
    <van-nav-bar
      title="商品列表"
      left-arrow
      @click-left="onBack"
    />
    <van-card
      num="2"
      price="2.00"
      desc="描述信息"
      title="商品标题"
      thumb="https://img.yzcdn.cn/vant/t-thirt.jpg"
    >
      <div slot="tags">
        <van-tag plain type="danger">T恤</van-tag>
        <van-tag plain type="danger">新款</van-tag>
      </div>
      <div slot="footer">
        <van-button size="mini">立即购买</van-button>
        <van-button size="mini">添加购物车</van-button>
      </div>
    </van-card>
  </div>
</template>

<script>
import Vue from 'vue'
import { Card, Tag } from 'vant'
Vue.use(Card).use(Tag)
export default {
  methods: {
    onBack: function () {
      this.$router.go(-1)
    }
  }
}
</script>
