import { createRouter, createWebHistory } from 'vue-router'
import store from '@/stores'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'logIn1',
      component: () => import('@/components/Login/logIn1.vue')
    },
    {
      path: '/login2',
      name: 'logIn2',
      component: () => import('@/components/Login/logIn2.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/components/Login/register.vue')
    },
    {
      path: '/resetpassword',
      name: 'resetpassword',
      component: () => import('@/components/Login/RetrievePassword.vue')
    },
    {
      path: '/registerSuccess',
      name: 'RegisterSuccess',
      component: () => import('@/components/Login/RegisgerSuccess.vue')
    },
    {
      path: '/RetrSuccess',
      name: 'RetrievePasswordSuccess',
      component: () => import('@/components/Login/RetrievePasswordSuccess.vue')
    },
    {
      path: '/Home',
      name: 'NormalHome',
      component: () => import('@/components/RegularUsers/Home.vue')
    },
    {
      path: '/EventInfo',
      name: 'Eventinfo',
      component: () => import('@/components/RegularUsers/EventInfo.vue')
    },
    {
      path: '/EventR',
      name: 'EventReservation',
      component: () => import('@/components/RegularUsers/EventRe.vue')
    },
    {
      path: '/Order',
      name: 'OrderCenter',
      component: () => import('@/components/RegularUsers/OrderCenter.vue')
    },
    {
      path: '/Notice',
      name: 'noticec',
      component: () => import('@/components/RegularUsers/NoticeCenter.vue')
    },
    {
      path: '/Personal',
      name: 'per',
      component: () => import('@/components/RegularUsers/PersonalCenter.vue')
    },
    {
      path: '/Visitor',
      name: 'vis',
      component: () => import('@/components/RegularUsers/VisitorInfo.vue')
    },
    {
      path: '/VenueHome',
      name: 'venuehome',
      component: () => import('@/components/VenueAdministrators/Home.vue')
    },
    {
      path: '/Venue2',
      name: 'venue2',
      component: () => import('@/components/VenueAdministrators/Home2.vue')
    },
    {
      path: '/venecharts',
      name: 've',
      component: () => import('@/components/VenueAdministrators/venecharts.vue')
    },
    {
      path: '/reshow',
      name: 'resh',
      component: () => import('@/components/VenueAdministrators/ReShow.vue')
    },
    {
      path: '/noticere',
      name: 'noticere',
      component: () => import('@/components/VenueAdministrators/noticerelase.vue')
    },
    {
      path: '/comment',
      name: 'com',
      component: () => import('@/components/VenueAdministrators/comments.vue')
    },
    {
      path: '/syshome',
      name: 'sysadmin',
      component: () => import('@/components/SystemAdminintrators/Home.vue')
    },
    {
      path: '/syshome2',
      name: 'sysadmin2',
      component: () => import('@/components/SystemAdminintrators/Home2.vue')
    },
    {
      path: '/sysactmanage',
      name: 'sysactmanage',
      component: () => import('@/components/SystemAdminintrators/sysactmanage.vue')
    },
    {
      path: '/sysactmanage2',
      name: 'sysactmanage2',
      component: () => import('@/components/SystemAdminintrators/sysactmanage2.vue')
    },
    {
      path: '/sysrelease',
      name: 'sysnotice',
      component: () => import('@/components/SystemAdminintrators/noticerelase.vue')
    },
    {
      path: '/sysactrelease',
      name: 'sysactnotice',
      component: () => import('@/components/SystemAdminintrators/actnoticerelase.vue')
    },
    {
      path: '/syscom',
      name: 'syscomments',
      component: () => import('@/components/SystemAdminintrators/comments.vue')
    },
    {
      path: '/sysreshow',
      name: 'sysre',
      component: () => import('@/components/SystemAdminintrators/ReShow.vue')
    },
    {
      path: '/sysecharts',
      name: 'sysechar',
      component: () => import('@/components/SystemAdminintrators/sysecharts.vue')
    },
    {
      path: '/mapuser',
      name: 'gdmapu',
      component: () => import('@/components/RegularUsers/MapUser.vue')
    },
    {
      path: '/mapsys',
      name: 'gdmaps',
      component: () => import('@/components/SystemAdminintrators/MapSys.vue')
    },
    {
      path: '/mapven',
      name: 'gdmapn',
      component: () => import('@/components/VenueAdministrators/MapVen.vue')
    },
    {
      path: '/:xxx(.*)*',
      name: '404',
      component: () => import('@/components/404.vue')
    },
  ]
})
router.beforeEach(async (to, from, next) => {
  const authRequiredRoutes = ['NormalHome', 'Eventinfo', 'EventReservation', 'OrderCenter', 'noticec', 'per', 'vis', 'venuehome', 'resh', 'noticere', 'sysadmin', 'sysactmanage', 'scan']
  if (authRequiredRoutes.includes(to.name) && !store.state.islogin) {
    ElMessage.error('未登录，无法跳转')
    next('/')
  }/*else if(from.name!="NormalHome"&&to.name=="Eventinfo"&&store.state.islogin){
    if(from.name!=to.name){
      ElMessage.error("未选择场馆")
      next('/Home')
    }
  }else if(from.name!="Eventinfo"&&to.name=="EventReservation"&&store.state.islogin){
    if(from.name!=to.name){
      ElMessage.error("未选择场馆或活动")
      next('/Home')
    }
  }*/
  else {
    next()
  }

})

export default router
