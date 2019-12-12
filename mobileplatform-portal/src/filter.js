import Vue from 'vue'

Vue.filter('menuType', function (type) {
  var msg = '状态异常'
  switch (type) {
    case 1:
      msg = '菜单'
      break
    case 2:
      msg = '按钮'
      break
  }
  return msg
})

Vue.filter('packageStatus', function (type) {
  var msg = '状态异常'
  switch (type) {
    case 1:
      msg = '待发行'
      break
    case 2:
      msg = '已发行'
      break
  }
  return msg
})

export default {}
