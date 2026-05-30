---
name: springboot-crud
description: 生成标准SpringBoot CRUD代码，包含Entity、Mapper、Service、Controller、DTO、VO、分页、校验
---

# SpringBoot CRUD Generator
根据表结构生成一整套后端代码：
1. Entity（带Lombok、注释）
2. Mapper（继承BaseMapper）
3. Service接口 + Impl
4. Controller（RESTful）
5. DTO、VO
6. 分页（Page）
7. 参数校验
8. 统一返回Result<T>

要求：
- 包名：com.example
- 路径：examination-system-backend/src/main/java/com/example/
- 端口：8080
- 数据库：MySQL8
- 使用MyBatis-Plus