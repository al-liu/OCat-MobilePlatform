<template>
  <div>
    <van-nav-bar
      title="转账"
      left-arrow
      @click-left="onBack"
    />
    <van-cell-group title="收款人">
      <van-field
        v-model="payeeName"
        clearable
        label="户名"
        right-icon="contact"
        placeholder="请输入收款人户名"
      />
      <van-field
        v-model="payeeAccount"
        clearable
        label="账号"
        right-icon="scan"
        placeholder="请输入收款人账号"
      />
      <van-field
        readonly
        clickable
        label="银行"
        :value="payeeBank"
        placeholder="选择银行"
        right-icon="arrow"
        @click="showPayeeBank = true"
      />
      <van-popup v-model="showPayeeBank" position="bottom">
        <van-picker
          show-toolbar
          :columns="payeeBankList"
          @cancel="showPayeeBank = false"
          @confirm="onConfirmPayeeBank"
        />
      </van-popup>
    </van-cell-group>
    <van-cell-group title="转账金额">
      <van-field
        v-model="money"
        clearable
        label="￥"
        placeholder="0手续费"
      />
      <van-field
        readonly
        clickable
        label="付款卡"
        :value="payerBank"
        placeholder="选择银行"
        right-icon="arrow"
        @click="showPayerBank = true"
      />
      <van-popup v-model="showPayerBank" position="bottom">
        <van-picker
          show-toolbar
          :columns="payerBankList"
          @cancel="showPayerBank = false"
          @confirm="onConfirmPayerBank"
        />
      </van-popup>
    </van-cell-group>
    <van-cell-group>
      <van-field
        v-model="sms"
        center
        clearable
        label="短信验证码"
        placeholder="请输入短信验证码"
      >
        <van-button slot="button" size="small" type="primary">发送验证码</van-button>
      </van-field>
    </van-cell-group>
    <div style="text-align:center">
      <van-button class="nextStep" color="linear-gradient(to right, #4bb0ff, #6149f6)" @click="nextStep">下一步</van-button>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { Picker, Field, Cell, CellGroup, Popup } from 'vant'
Vue.use(Field).use(Picker).use(Cell).use(CellGroup).use(Popup)
export default {
  data () {
    return {
      payeeName: '',
      payeeAccount: '',
      payeeBank: '',
      money: '',
      payerBank: '',
      sms: '',
      showPayeeBank: false,
      payeeBankList: ['招商银行', '中信银行', '民商银行'],
      showPayerBank: false,
      payerBankList: ['6832938293892393829 - ￥100.00', '68329382938923938322 - ￥22.10']
    }
  },
  methods: {
    onBack: function () {
      this.$router.go(-1)
    },
    onConfirmPayeeBank: function (value) {
      this.payeeBank = value
      this.showPayeeBank = false
    },
    onConfirmPayerBank: function (value) {
      this.payerBank = value
      this.showPayerBank = false
    },
    nextStep: function () {
      this.$router.push('/transferResult')
    }
  }
}
</script>
<style scoped>
  .nextStep {
    width: 90%;
    margin-top: 40px
  }
</style>
