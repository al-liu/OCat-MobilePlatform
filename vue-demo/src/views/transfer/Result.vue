<template>
  <div>
    <van-nav-bar
      title="转账结果"
      left-arrow
      @click-left="onBack"
    />
    <h1>转账成功</h1>
  </div>
</template>

<script>
export default {
  methods: {
    onBack: function () {
      this.$router.go(-1)
    }
  }
}
</script>
