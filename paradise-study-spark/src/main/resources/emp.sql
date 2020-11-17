/*
 Navicat Premium Data Transfer

 Source Server         : localmysql
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 17/11/2020 22:41:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for emp
-- ----------------------------
DROP TABLE IF EXISTS `emp`;
CREATE TABLE `emp`  (
  `empno` int(0) NOT NULL,
  `ename` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `job` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `mgr` int(0) NULL DEFAULT NULL,
  `hiredate` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `sal` double(10, 2) NULL DEFAULT NULL,
  `comm` double(10, 2) NULL DEFAULT NULL,
  `deptno` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`empno`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of emp
-- ----------------------------
INSERT INTO `emp` VALUES (1, 'SMITH', 'CLERK', 10, '2020-11-17 22:35:13', 800.00, NULL, 20);
INSERT INTO `emp` VALUES (2, 'ALLEN', 'SALESMAN', 9, '2020-11-17 22:35:53', 1600.00, 300.00, 30);
INSERT INTO `emp` VALUES (3, 'WARD', 'SALESMAN', 9, '2020-11-17 22:36:02', 1250.00, 500.00, 30);
INSERT INTO `emp` VALUES (4, 'JONES', 'MANAGER', 8, '2020-11-17 22:36:49', 2975.00, NULL, 20);
INSERT INTO `emp` VALUES (5, 'MARTIN', 'SALESMAN', 9, '2020-11-17 22:37:15', 1250.00, 1400.00, 30);
INSERT INTO `emp` VALUES (6, 'BLAKE', 'MANAGER', 8, '2020-11-17 22:37:43', 2850.00, NULL, 30);
INSERT INTO `emp` VALUES (7, 'CLARK', 'MANAGER', 8, '2020-11-17 22:38:14', 2450.00, NULL, 10);
INSERT INTO `emp` VALUES (8, 'KING', 'PRESIDENT', NULL, '2020-11-17 22:38:54', 5000.00, NULL, 10);
INSERT INTO `emp` VALUES (9, 'FORD', 'ANALYST', 8, '2020-11-17 22:39:38', 3000.00, NULL, 20);
INSERT INTO `emp` VALUES (10, 'HIVE', 'PROGARM', 8, '2020-11-17 22:40:03', 10300.00, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
