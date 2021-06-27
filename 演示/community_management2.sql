/*
 Navicat Premium Data Transfer

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 127.0.0.1:3306
 Source Schema         : community_management2

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 27/06/2021 15:44:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_assn_info
-- ----------------------------
DROP TABLE IF EXISTS `t_assn_info`;
CREATE TABLE `t_assn_info`  (
  `aid` int(11) NOT NULL AUTO_INCREMENT COMMENT '社团编号',
  `assn_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社团名称',
  `assn_founder` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社团创始人',
  `assn_leader` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '现任社长',
  `assn_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社团成立时间',
  `assn_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社团内容',
  `assn_brief` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社团简介',
  `assn_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社团地址',
  PRIMARY KEY (`aid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_assn_info
-- ----------------------------
INSERT INTO `t_assn_info` VALUES (1, '文化活动社', '张宇', '张宇', '2019-01-01', '略', '略', '略');
INSERT INTO `t_assn_info` VALUES (2, '影视后期社', '胡毅', '胡毅', '2019-03-06', '略', '略', '略');
INSERT INTO `t_assn_info` VALUES (3, '计算机社', '赵鑫', '赵鑫', '2019-01-01', '略', '略', '略');
INSERT INTO `t_assn_info` VALUES (4, '戏剧社团', '王胜', '王胜', '2016-03-03', '略', '略', '略');

-- ----------------------------
-- Table structure for t_course_info
-- ----------------------------
DROP TABLE IF EXISTS `t_course_info`;
CREATE TABLE `t_course_info`  (
  `cid` int(11) NOT NULL AUTO_INCREMENT COMMENT '课程编号',
  `aid` int(11) NOT NULL COMMENT '社团编号',
  `tid` int(11) NOT NULL COMMENT '教师编号',
  `c_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '课程名称',
  `c_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '开课学期',
  `c_credit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学分',
  `c_place` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '授课地点',
  `c_selected` int(11) NOT NULL COMMENT '已选人数',
  `c_amount` int(11) NOT NULL COMMENT '课程总人数',
  PRIMARY KEY (`cid`) USING BTREE,
  INDEX `tid`(`tid`) USING BTREE,
  INDEX `aid`(`aid`) USING BTREE,
  CONSTRAINT `t_course_info_ibfk_1` FOREIGN KEY (`tid`) REFERENCES `t_teacher_info` (`tid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_course_info_ibfk_2` FOREIGN KEY (`aid`) REFERENCES `t_assn_info` (`aid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_course_info
-- ----------------------------
INSERT INTO `t_course_info` VALUES (1, 1, 2, '国学经典', '2019', '4', '2101', 0, 40);
INSERT INTO `t_course_info` VALUES (2, 1, 2, '高等数学', '2019', '4', '2102', 0, 40);
INSERT INTO `t_course_info` VALUES (3, 3, 1, 'SQL Server数据库技术应用', '2019', '4', '2103', 2, 40);
INSERT INTO `t_course_info` VALUES (4, 3, 1, 'web前端开发', '2019', '4', '2201', 3, 40);
INSERT INTO `t_course_info` VALUES (5, 3, 1, '计算机操作系统原理', '2019', '4', '2202', 3, 40);
INSERT INTO `t_course_info` VALUES (6, 3, 1, 'C语言程序设计', '2019', '4', '2203', 3, 40);
INSERT INTO `t_course_info` VALUES (7, 3, 1, '局域网交换技术', '2019', '2', '1101', 1, 40);
INSERT INTO `t_course_info` VALUES (8, 3, 1, 'PHP动态网页设计', '2019', '2', '1102', 3, 40);
INSERT INTO `t_course_info` VALUES (9, 3, 1, '网络工程规划与设计', '2019', '4', '6104', 1, 40);
INSERT INTO `t_course_info` VALUES (10, 3, 1, 'Java面向对象程序设计', '2019', '4', '2105', 0, 40);
INSERT INTO `t_course_info` VALUES (11, 3, 1, '移动互联网时代的信息安全与防护', '2019', '4', '2105', 1, 40);
INSERT INTO `t_course_info` VALUES (12, 3, 2, '信息系统与数据库技术', '2019', '4', '2106', 1, 40);

-- ----------------------------
-- Table structure for t_elective_info
-- ----------------------------
DROP TABLE IF EXISTS `t_elective_info`;
CREATE TABLE `t_elective_info`  (
  `eid` int(11) NOT NULL AUTO_INCREMENT COMMENT '选课编号',
  `sid` int(11) NOT NULL COMMENT '学生编号',
  `cid` int(11) NOT NULL COMMENT '课程编号',
  `aid` int(11) NOT NULL COMMENT '社团编号',
  PRIMARY KEY (`eid`) USING BTREE,
  INDEX `sid`(`sid`) USING BTREE,
  INDEX `cid`(`cid`) USING BTREE,
  INDEX `aid`(`aid`) USING BTREE,
  CONSTRAINT `t_elective_info_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `t_student_info` (`sid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_elective_info_ibfk_2` FOREIGN KEY (`cid`) REFERENCES `t_course_info` (`cid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_elective_info_ibfk_3` FOREIGN KEY (`aid`) REFERENCES `t_assn_info` (`aid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_elective_info
-- ----------------------------
INSERT INTO `t_elective_info` VALUES (20, 3, 4, 3);
INSERT INTO `t_elective_info` VALUES (21, 3, 5, 3);
INSERT INTO `t_elective_info` VALUES (22, 3, 6, 3);
INSERT INTO `t_elective_info` VALUES (24, 3, 8, 3);
INSERT INTO `t_elective_info` VALUES (28, 3, 12, 3);
INSERT INTO `t_elective_info` VALUES (33, 2, 6, 3);
INSERT INTO `t_elective_info` VALUES (34, 2, 4, 3);
INSERT INTO `t_elective_info` VALUES (35, 2, 5, 3);
INSERT INTO `t_elective_info` VALUES (36, 2, 7, 3);
INSERT INTO `t_elective_info` VALUES (37, 2, 8, 3);
INSERT INTO `t_elective_info` VALUES (39, 2, 11, 3);
INSERT INTO `t_elective_info` VALUES (40, 2, 3, 3);
INSERT INTO `t_elective_info` VALUES (43, 1, 3, 3);
INSERT INTO `t_elective_info` VALUES (44, 1, 4, 3);
INSERT INTO `t_elective_info` VALUES (45, 1, 5, 3);
INSERT INTO `t_elective_info` VALUES (46, 1, 6, 3);
INSERT INTO `t_elective_info` VALUES (47, 1, 9, 3);
INSERT INTO `t_elective_info` VALUES (48, 1, 8, 3);

-- ----------------------------
-- Table structure for t_student_info
-- ----------------------------
DROP TABLE IF EXISTS `t_student_info`;
CREATE TABLE `t_student_info`  (
  `sid` int(11) NOT NULL AUTO_INCREMENT COMMENT '学生编号',
  `aid` int(255) NOT NULL COMMENT '社团编号',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学生账号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学生密码',
  `realname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学生姓名',
  `sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学生性别',
  `year` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学生年龄',
  `faculty` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属院系',
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话号码',
  PRIMARY KEY (`sid`) USING BTREE,
  INDEX `aid`(`aid`) USING BTREE,
  CONSTRAINT `t_student_info_ibfk_1` FOREIGN KEY (`aid`) REFERENCES `t_assn_info` (`aid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_student_info
-- ----------------------------
INSERT INTO `t_student_info` VALUES (1, 3, '01', '123456', '胡杨', '男', '19', '电气学院', '13976581245');
INSERT INTO `t_student_info` VALUES (2, 3, '02', '123456', '胡芳', '女', '19', '电气学院', '13976581365');
INSERT INTO `t_student_info` VALUES (3, 3, '03', '123456', '李四', '男', '20', '信息工程学院', '13786542589');

-- ----------------------------
-- Table structure for t_teacher_info
-- ----------------------------
DROP TABLE IF EXISTS `t_teacher_info`;
CREATE TABLE `t_teacher_info`  (
  `tid` int(11) NOT NULL AUTO_INCREMENT COMMENT '教师编号',
  `t_username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '教师账号',
  `t_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '教师密码',
  `t_realname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '教师姓名',
  `t_sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '教师性别',
  `t_age` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '教师年龄',
  `t_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话号码',
  PRIMARY KEY (`tid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_teacher_info
-- ----------------------------
INSERT INTO `t_teacher_info` VALUES (1, 'admin', '123456', '系统管理员', '男', '22', '13786541234');
INSERT INTO `t_teacher_info` VALUES (2, 't1', '123456', '胡毅', '男', '28', '13785642356');
INSERT INTO `t_teacher_info` VALUES (3, 't2', '123456', '赵飞', '女', '28', '13785632564');

SET FOREIGN_KEY_CHECKS = 1;
