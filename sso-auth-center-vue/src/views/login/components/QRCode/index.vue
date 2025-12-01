<template>
    <div>
      <div v-loading="loading" class="login-wechat-code">
        <QrcodeVue :value="authCode" :size="250" />
        <div
          class="login-wechat-code-icon"
          v-if="scanCode === 1 || scanCode === 2 || scanCode === -1"
        >
          <div v-if="scanCode === -1">
            <i class="el-icon-circle-close" style="font-size: 75px; color: red;"></i>
            <span class="login-wechat-text error">二维码获取失败</span>
          </div>
          <div v-else>
            <i class="el-icon-success" style="font-size: 75px; color: #16d216;"></i>
            <span class="login-wechat-text success">扫码成功</span>
          </div>
        </div>
      </div>
      <div class="login-tips">
        <slot name="tips">
          <span class="login-tips-text">{{ tips }}</span>
        </slot>
        <el-tooltip content="刷新二维码" placement="bottom">
          <i
            class="el-icon-refresh refresh"
            style="font-size: 18px; color: #303030; cursor: pointer;"
            :style="{ 'animation-play-state': loading ? 'running' : 'paused' }"
            @click="() => doRefresh()"
          ></i>
        </el-tooltip>
      </div>
    </div>
  </template>
  <script>
  import QrcodeVue from 'qrcode.vue'
  export default {
    name: 'QRCode',
    components: {
      QrcodeVue
    },
    props: {
      loading: Boolean,
      doRefresh: Function,
      scanCode: Number,
      tips: String,
      authCode: String
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .login-wechat-code {
    position: relative;
    &-icon {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      margin: auto;
      background-color: rgba(255, 255, 255, 0.9);
      display: flex;
      align-items: center;
      justify-content: center;
      & > div {
        display: flex;
        flex-direction: column;
        align-items: center;
        .login-wechat-text {
          &.error {
            color: red;
          }
          &.success {
            color: #3fd215;
          }
        }
      }
    }
  }
  
  .login-tips {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 10px;
    font-size: 13px;
    .login-tips-text {
      margin-right: 15px;
      color: #303030;
    }
    i.refresh {
      cursor: pointer;
      animation: loop 1s infinite forwards;
    }
  }
  
  @keyframes loop {
    0% {
      transform: rotate(0);
    }
  
    100% {
      transform: rotate(360deg);
    }
  }
  </style>
  