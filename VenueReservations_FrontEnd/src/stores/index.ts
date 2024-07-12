import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

const store = createStore({
  state() {
    return {
      id: '0',
      user_home_card_id: '0',
      user_event_card_id: '0',
      token: '0',
      type: '0',
      islogin: false,
      muble_search: '0',
      location: '安徽省合肥市'
    }
  },
  mutations: {
    setid(state, id) {
      state.id = id
    },
    setuserhomecardid(state, cardid) {
      state.user_home_card_id = cardid
    },
    setusereventcardid(state, cardevid) {
      state.user_event_card_id = cardevid
    },
    settoken(state, token) {
      state.token = token
    },
    settype(state, type) {
      state.type = type
    },
    setlogin(state, islogin) {
      state.islogin = islogin
    },
    setmublesearch(state, seco) {
      state.muble_search = seco
    },
    setlocation(state, lo) {
      state.location = lo
    }
  },
  actions: {
    id_save({ commit }, id) {
      commit('setid', id)
    },
    user_home_card_id_save({ commit }, card_id) {
      commit('setuserhomecardid', card_id)
    },
    user_event_card_id_save({ commit }, card_evid) {
      commit('setusereventcardid', card_evid)
    },
    token_save({ commit }, token) {
      commit('settoken', token)
    },
    type_save({ commit }, type) {
      commit('settype', type)
    },
    login_save({ commit }, status) {
      commit('setlogin', status)
    },
    muble_save({ commit }, seco) {
      commit('setmublesearch', seco)
    },
    location_save({ commit }, lo) {
      commit('setlocation', lo)
    }
  },
  plugins: [createPersistedState({ storage: sessionStorage })]
})

export default store
