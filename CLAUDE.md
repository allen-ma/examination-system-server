# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 技术栈
- Java 17, SpringBoot 3.2.x, MyBatis-Plus 3.5.x, Maven 3.9.x, MySQL 8.0
- Lombok, Jakarta Validation
- 统一返回：Result<T> (code/message/data)
- 端口：8080

## 目录结构
src/main/java/com/example/
├── ExaminationApplication.java          # 启动类 + @MapperScan
├── common/Result.java                   # 统一响应封装
├── config/
│   ├── CorsConfig.java                  # 全局跨域
│   ├── GlobalExceptionHandler.java      # 统一异常处理
│   └── MyMetaObjectHandler.java         # MyBatis-Plus 自动填充
├── entity/
│   ├── BaseEntity.java                  # id, createTime, updateTime
│   └── User.java                        # 示例实体
├── mapper/UserMapper.java               # BaseMapper<User>
├── dto/UserDTO.java                     # 入参校验 DTO
├── vo/UserVO.java                       # 出参 VO（不含密码）
├── service/UserService.java             # 接口
├── service/impl/UserServiceImpl.java    # 实现
└── controller/UserController.java       # RESTful CRUD

src/main/resources/
├── application.yml                      # 配置
└── db/schema.sql                        # 建表语句

## 编码规范
- RESTful 接口，路径 /api/v1/xxx
- Lombok，禁止手写 getter/setter
- 入参校验 @NotBlank、@NotNull
- @ControllerAdvice 统一异常处理
- MyBatis-Plus LambdaQueryWrapper，禁止 SELECT *
- 统一返回 Result<T>：success()/error()
- 新增/修改实体通过 DTO，返回通过 VO
- 分页用 MyBatis-Plus Page
- 使用 jakarta.* 而非 javax.*（SpringBoot 3.x）

## 常用命令
- 后端启动：mvn spring-boot:run
- 数据库初始化：执行 src/main/resources/db/schema.sql
- 创建数据库：CREATE DATABASE examination_system DEFAULT CHARACTER SET utf8mb4;