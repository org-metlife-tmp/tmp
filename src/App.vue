<style>
    #app {
        height: 100%;
    }
</style>
<template>
    <div id="app">
        <router-view></router-view>
    </div>
</template>

<script>
    //    axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

    //全局注入store
    import store from './js/store.js'

    export default {
        name: 'App',
        data: function () {
            return {}
        },
        methods: {},
        store:store
    }
</script>

