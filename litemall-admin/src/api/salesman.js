import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/salesman/list',
    method: 'get',
    params: query
  })
}

export function deleteSalesman(data) {
  return request({
    url: '/salesman/delete',
    method: 'post',
    data
  })
}

export function auditSalesman(data) {
  return request({
    url: '/salesman/audit',
    method: 'post',
    data
  })
}

