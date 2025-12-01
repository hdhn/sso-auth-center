import request from '@/utils/request'

/**
 * 获取微信授权链接
 * 1-首页登陆授权，2-用户账号页面授权
 * @returns object {authorizeUrl: string, state: string} 
 */
export function getAuthLink(mode) {
  return request({
    url: `/wechat/create/code/${mode}`,
    method: 'GET',
  })
}

/**
 * 判定用户是否首次绑定
 * 0-未关注公众号
 * 1-首次绑定
 * 2-已经绑定
 * 3-授权中
 * 4-授权失败
 * @returns object {code: string, token: string} 
 */
export function getAuthResult(params) {
  return request({
    url: `/wechat/authorize/bindings/state`,
    method: 'GET',
    params
  })
}

/**
 * 绑定发送验证码
 * @param {phone: string, state: string} data 
 * @returns 
 */
export function sendMsg(data) {
  return request({
    url: '/wechat/authorize/bindings/msg',
    method: 'post',
    data
  })
}

/**
 * 绑定发送验证码
 * @param {phone: string, state: string, code: string} data 
 * @returns 
 */
export function bindWeChatAndLogin(data) {
  console.log(111)
  return request({
    url: '/wechat/authorize/bindings/login',
    method: 'post',
    data
  })
}


/**
 * 解绑
 * @param {string} phone
 * @returns 
 */
export function unBindWeChat(phone) {
  return request({
    url: `/wechat/authorize/unbind/${phone}`,
    method: 'get',
  })
}