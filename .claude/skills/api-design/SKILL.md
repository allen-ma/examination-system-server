---
name: api-design
description: 设计RESTful API，统一请求/响应格式，处理跨域、JWT
---

# API Design Standard
## 统一响应
{
"code": 200,
"message": "success",
"data": {}
}

## 状态码
- 200：成功
- 400：参数错误
- 401：未登录
- 403：无权限
- 500：服务器错误

## 接口规范
- GET /api/v1/xxx：查询列表
- GET /api/v1/xxx/{id}：查询详情
- POST /api/v1/xxx：新增
- PUT /api/v1/xxx/{id}：更新
- DELETE /api/v1/xxx/{id}：删除

## 跨域
全局CorsConfig，允许所有来源、header、method