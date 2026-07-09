docker : mysqldump: [Warning] Using a password on the command line interface can be insecure.
所在位置 D:\ChineseSoftWork\realwork\online-education\sql\backup\backup_mysql.ps1:15 字符: 11
+ $result = docker exec $containerName sh -c $dumpCmd 2>&1
+           ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    + CategoryInfo          : NotSpecified: (mysqldump: [War...an be insecure.:String) [], RemoteException
    + FullyQualifiedErrorId : NativeCommandError
 
-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: localhost    Database: edu
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `edu`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `edu` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `edu`;

--
-- Table structure for table `tb_assignment`
--

DROP TABLE IF EXISTS `tb_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '鎵€灞炶绋婭D',
  `teacher_id` bigint NOT NULL COMMENT '鍙戝竷鏁欏笀ID',
  `title` varchar(200) NOT NULL COMMENT '浣滀笟鏍囬',
  `content` text COMMENT '浣滀笟鍐呭',
  `full_score` int DEFAULT '100' COMMENT '婊″垎',
  `deadline` datetime DEFAULT NULL COMMENT '鎴鏃堕棿',
  `allow_submit_count` int DEFAULT '1' COMMENT '鍏佽鎻愪氦娆℃暟 1/2/3',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣滀笟琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_assignment`
--

LOCK TABLES `tb_assignment` WRITE;
/*!40000 ALTER TABLE `tb_assignment` DISABLE KEYS */;
INSERT INTO `tb_assignment` VALUES (1,1,2,'Java鍩虹缁冧範','缂栧啓涓€涓狫ava绋嬪簭瀹炵幇瀛︾敓鎴愮哗绠＄悊绯荤粺',100,'2026-04-15 23:59:59',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(2,1,2,'闈㈠悜瀵硅薄缂栫▼浣滀笟','璁捐涓€涓摱琛岃处鎴风郴缁燂紝浣跨敤缁ф壙涓庡鎬?,100,'2026-05-15 23:59:59',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(3,2,2,'绾挎€ц〃缁冧範','瀹炵幇椤哄簭琛ㄥ拰閾捐〃鐨勬彃鍏ャ€佸垹闄ゆ搷浣?,100,'2026-04-20 23:59:59',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(4,4,7,'Python鍩虹缂栫▼缁冧範','缂栧啓涓€涓鐢熸垚缁╃粺璁＄▼搴忥細杈撳叆澶氫釜瀛︾敓鐨勬垚缁╋紝杈撳嚭鏈€楂樺垎銆佹渶浣庡垎銆佸钩鍧囧垎鍙婂強鏍间汉鏁?,100,'2026-07-20 23:59:59',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(5,4,7,'Python鏁版嵁鍒嗘瀽浣滀笟','浣跨敤Pandas璇诲彇CSV鏂囦欢锛屽畬鎴愭暟鎹竻娲椼€佺粺璁″垎鏋愬拰鍙鍖?,100,'2026-08-01 23:59:59',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(6,5,7,'杩涚▼璋冨害绠楁硶妯℃嫙','缂栫▼瀹炵幇FCFS銆丼JF銆丷R涓夌杩涚▼璋冨害绠楁硶锛屾瘮杈冨叾骞冲潎鍛ㄨ浆鏃堕棿',100,'2026-07-25 23:59:59',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(7,5,7,'鍐呭瓨绠＄悊浣滀笟','妯℃嫙鍒嗛〉瀛樺偍绠＄悊涓殑鍦板潃杞崲杩囩▼锛屽疄鐜癓RU椤甸潰缃崲绠楁硶',100,'2026-08-05 23:59:59',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(8,1,2,'Java寮傚父澶勭悊缁冧範','缂栧啓绋嬪簭婕旂ずtry-catch-finally鐨勪娇鐢紝鑷畾涔変竴涓笟鍔″紓甯哥被',100,'2026-06-30 23:59:59',2,'2026-07-09 14:34:43','2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_chapter`
--

DROP TABLE IF EXISTS `tb_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_chapter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '鎵€灞炶绋婭D',
  `title` varchar(200) NOT NULL COMMENT '绔犺妭鏍囬',
  `summary` text COMMENT '绔犺妭鎽樿',
  `sort_order` int DEFAULT '0' COMMENT '鎺掑簭',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_course` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='绔犺妭琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_chapter`
--

LOCK TABLES `tb_chapter` WRITE;
/*!40000 ALTER TABLE `tb_chapter` DISABLE KEYS */;
INSERT INTO `tb_chapter` VALUES (1,1,'Java璇█鍩虹','Java鍙戝睍鍙层€佺幆澧冩惌寤恒€佸熀鏈娉曘€佹暟鎹被鍨嬨€佽繍绠楃銆佹祦绋嬫帶鍒?,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(2,1,'闈㈠悜瀵硅薄缂栫▼','绫讳笌瀵硅薄銆佸皝瑁呫€佺户鎵裤€佸鎬併€佹帴鍙ｃ€佹娊璞＄被',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(3,1,'寮傚父澶勭悊涓庡父鐢ㄧ被','寮傚父鏈哄埗銆丼tring绫汇€侀泦鍚堟鏋跺垵姝?,3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(4,1,'Java杈撳叆杈撳嚭','File绫汇€佸瓧鑺傛祦涓庡瓧绗︽祦銆佺紦鍐叉祦',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(5,2,'绾挎€ц〃','椤哄簭琛ㄣ€侀摼琛ㄣ€佹爤銆侀槦鍒楃殑瀹氫箟銆佸瓨鍌ㄧ粨鏋勪笌鍩烘湰鎿嶄綔',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(6,2,'鏍戜笌浜屽弶鏍?,'鏍戠殑瀹氫箟銆佷簩鍙夋爲閬嶅巻銆佸搱澶浖鏍?,2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(7,2,'鍥?,'鍥剧殑瀛樺偍銆侀亶鍘嗐€佹渶灏忕敓鎴愭爲銆佹渶鐭矾寰?,3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(8,2,'鏌ユ壘涓庢帓搴?,'浜屽垎鏌ユ壘銆佸搱甯岃〃銆佸啋娉℃帓搴忋€佸揩閫熸帓搴忋€佸綊骞舵帓搴?,4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(9,4,'Python鍩虹璇硶涓庢暟鎹被鍨?,NULL,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(10,4,'Python闈㈠悜瀵硅薄缂栫▼',NULL,2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(11,4,'Python鏂囦欢鎿嶄綔涓庡紓甯稿鐞?,NULL,3,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(12,4,'Python鏁版嵁鍒嗘瀽鍏ラ棬',NULL,4,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(13,5,'杩涚▼涓庣嚎绋嬬鐞?,NULL,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(14,5,'鍐呭瓨绠＄悊',NULL,2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(15,5,'鏂囦欢绯荤粺',NULL,3,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(16,5,'I/O璁惧绠＄悊',NULL,4,'2026-07-09 14:34:43','2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_course`
--

DROP TABLE IF EXISTS `tb_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) NOT NULL COMMENT '璇剧▼鍚嶇О',
  `description` text COMMENT '璇剧▼绠€浠?,
  `teacher_id` bigint NOT NULL COMMENT '鎺堣鏁欏笀ID',
  `credit` int DEFAULT '0' COMMENT '瀛﹀垎',
  `max_students` int DEFAULT '100' COMMENT '鏈€澶ч€夎浜烘暟',
  `enrolled_count` int DEFAULT '0' COMMENT '宸查€変汉鏁?,
  `status` varchar(2) DEFAULT '1' COMMENT '0-寰呭鏍?1-鍗冲皢寮€璇?2-鎺堣涓?3-宸茬粨璇?4-瀹℃牳涓嶉€氳繃',
  `homework_ratio` int DEFAULT '50' COMMENT '骞虫椂浣滀笟鍗犳瘮%',
  `exam_ratio` int DEFAULT '50' COMMENT '鏈熸湯鑰冭瘯鍗犳瘮%',
  `exam_time` datetime DEFAULT NULL COMMENT '鑰冭瘯鏃堕棿',
  `start_time` datetime DEFAULT NULL COMMENT '寮€璇炬椂闂?,
  `end_time` datetime DEFAULT NULL COMMENT '缁撹鏃堕棿',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_course`
--

LOCK TABLES `tb_course` WRITE;
/*!40000 ALTER TABLE `tb_course` DISABLE KEYS */;
INSERT INTO `tb_course` VALUES (1,'Java绋嬪簭璁捐','Java鍩虹涓庨潰鍚戝璞＄紪绋?,2,4,100,1,'2',40,60,'2026-06-20 14:00:00','2026-03-01 00:00:00','2026-07-01 00:00:00','2026-07-09 14:22:00','2026-07-09 14:22:00'),(2,'鏁版嵁缁撴瀯','甯歌鏁版嵁缁撴瀯涓庣畻娉?,2,3,80,1,'2',50,50,'2026-06-25 14:00:00','2026-03-01 00:00:00','2026-07-01 00:00:00','2026-07-09 14:22:00','2026-07-09 14:22:00'),(3,'鏁版嵁搴撳師鐞?,'鍏崇郴鏁版嵁搴撲笌SQL璇█',2,3,80,0,'1',50,50,NULL,'2026-09-01 00:00:00','2027-01-01 00:00:00','2026-07-09 14:22:00','2026-07-09 14:22:00'),(4,'Python绋嬪簭璁捐','Python璇█鍩虹銆侀潰鍚戝璞°€佹暟鎹垎鏋愬叆闂?,7,0,100,0,'2',50,50,NULL,NULL,NULL,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(5,'鎿嶄綔绯荤粺鍘熺悊','杩涚▼绠＄悊銆佸唴瀛樼鐞嗐€佹枃浠剁郴缁熴€両/O绠＄悊',7,0,100,0,'2',50,50,NULL,NULL,NULL,'2026-07-09 14:34:43','2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_course_selection`
--

DROP TABLE IF EXISTS `tb_course_selection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_course_selection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID',
  `score` int DEFAULT NULL COMMENT '鎬昏瘎鎴愮哗',
  `status` varchar(2) DEFAULT '0' COMMENT '0-寰呭鏍?1-宸查€夎 2-宸查€€閫?3-宸茬粨璇?4-瀹℃牳涓嶉€氳繃',
  `select_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '閫夎鏃堕棿',
  `drop_time` datetime DEFAULT NULL COMMENT '閫€閫夋椂闂?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stu_course` (`student_id`,`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫夎璁板綍琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_course_selection`
--

LOCK TABLES `tb_course_selection` WRITE;
/*!40000 ALTER TABLE `tb_course_selection` DISABLE KEYS */;
INSERT INTO `tb_course_selection` VALUES (1,1,1,NULL,'1','2026-07-09 14:22:00',NULL),(2,1,2,NULL,'1','2026-07-09 14:22:00',NULL),(3,5,4,NULL,'1','2026-06-20 08:30:00',NULL),(4,5,5,NULL,'1','2026-06-21 09:00:00',NULL),(5,6,4,NULL,'1','2026-06-22 10:00:00',NULL),(6,8,5,NULL,'1','2026-06-23 11:00:00',NULL),(7,1,4,NULL,'1','2026-06-24 08:00:00',NULL),(8,4,5,NULL,'1','2026-06-25 09:30:00',NULL);
/*!40000 ALTER TABLE `tb_course_selection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_forum_post`
--

DROP TABLE IF EXISTS `tb_forum_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_forum_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '鎵€灞炶绋婭D',
  `user_id` bigint NOT NULL COMMENT '鍙戝笘浜篒D',
  `title` varchar(200) NOT NULL COMMENT '甯栧瓙鏍囬',
  `content` text COMMENT '甯栧瓙鍐呭',
  `reply_count` int DEFAULT '0' COMMENT '鍥炲鏁?,
  `status` tinyint DEFAULT '1' COMMENT '0-寰呭鏍?1-宸插彂甯?2-宸茬疆椤?,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璁哄潧甯栧瓙琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_forum_post`
--

LOCK TABLES `tb_forum_post` WRITE;
/*!40000 ALTER TABLE `tb_forum_post` DISABLE KEYS */;
INSERT INTO `tb_forum_post` VALUES (1,1,1,'Java浣滀笟姹傚姪','璇烽棶鑰佸笀锛屽皝瑁呭拰缁ф壙鐨勫尯鍒槸浠€涔堬紵',0,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(2,1,1,'澶氭€佹蹇典笉鐞嗚В','杩愯鏃跺鎬佸拰缂栬瘧鏃跺鎬佹湁浠€涔堝尯鍒紵',0,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(3,2,1,'浜屽弶鏍戦亶鍘嗛棶棰?,'鍓嶅簭銆佷腑搴忋€佸悗搴忛亶鍘嗙殑閫掑綊瀹炵幇鎬庝箞鍐欙紵',0,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(4,4,5,'Python鏁版嵁鍒嗘瀽姹傚姪','璇烽棶鑰佸笀锛孭andas璇诲彇CSV鏃朵腑鏂囦贡鐮佹€庝箞瑙ｅ喅锛?,0,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(5,4,6,'NumPy鏁扮粍杩愮畻闂','骞挎挱鏈哄埗涓嶅お鐞嗚В锛屾湁娌℃湁閫氫織鐨勮В閲婏紵',0,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(6,5,8,'杩涚▼璋冨害闂','RR璋冨害鐨勬椂闂寸墖澶у皬濡備綍閫夋嫨锛?,0,1,'2026-07-09 14:34:43','2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_forum_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_forum_reply`
--

DROP TABLE IF EXISTS `tb_forum_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_forum_reply` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '甯栧瓙ID',
  `user_id` bigint NOT NULL COMMENT '鍥炲浜篒D',
  `content` text NOT NULL COMMENT '鍥炲鍐呭',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璁哄潧鍥炲琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_forum_reply`
--

LOCK TABLES `tb_forum_reply` WRITE;
/*!40000 ALTER TABLE `tb_forum_reply` DISABLE KEYS */;
INSERT INTO `tb_forum_reply` VALUES (1,1,2,'灏佽鏄殣钘忓唴閮ㄥ疄鐜扮粏鑺傦紝缁ф壙鏄瓙绫昏幏鍙栫埗绫荤殑灞炴€у拰鏂规硶銆?,'2026-07-09 14:22:00'),(2,1,4,'琛ュ厖涓€涓嬶細灏佽閫氳繃private瀹炵幇锛岀户鎵块€氳繃extends瀹炵幇銆?,'2026-07-09 14:22:00'),(3,3,2,'鍓嶅簭锛氭牴宸﹀彸锛涗腑搴忥細宸︽牴鍙筹紱鍚庡簭锛氬乏鍙虫牴銆傞€掑綊瀹炵幇寰堢畝鍗曘€?,'2026-07-09 14:22:00'),(4,4,7,'鍦╮ead_csv()涓坊鍔爀ncoding=\"utf-8\"鎴杄ncoding=\"gbk\"鍙傛暟鍗冲彲銆?,'2026-07-09 14:34:43'),(5,5,7,'骞挎挱鏈哄埗绠€鍗曟潵璇村氨鏄舰鐘朵笉鍚岀殑鏁扮粍杩涜杩愮畻鏃讹紝NumPy浼氳嚜鍔ㄦ墿灞曠淮搴﹁緝灏忕殑鏁扮粍銆?,'2026-07-09 14:34:43'),(6,6,7,'鏃堕棿鐗囬€氬父璁句负20-50ms锛屽お鐭細瀵艰嚧棰戠箒涓婁笅鏂囧垏鎹紝澶暱浼氶檷浣庡搷搴旈€熷害銆?,'2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_forum_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_knowledge_point`
--

DROP TABLE IF EXISTS `tb_knowledge_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_knowledge_point` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_id` bigint NOT NULL COMMENT '鎵€灞炵珷鑺侷D',
  `content` varchar(500) NOT NULL COMMENT '鐭ヨ瘑鐐瑰唴瀹?,
  `importance` varchar(10) DEFAULT 'medium' COMMENT '閲嶈鎬? high/medium/low',
  `sort_order` int DEFAULT '0' COMMENT '鎺掑簭',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chapter` (`chapter_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐭ヨ瘑鐐硅〃';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_knowledge_point`
--

LOCK TABLES `tb_knowledge_point` WRITE;
/*!40000 ALTER TABLE `tb_knowledge_point` DISABLE KEYS */;
INSERT INTO `tb_knowledge_point` VALUES (1,1,'Java鍙戝睍鍘嗗彶涓庤瑷€鐗圭偣','low',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(2,1,'JDK瀹夎涓庣幆澧冨彉閲忛厤缃?,'medium',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(3,1,'鍩烘湰鏁版嵁绫诲瀷涓庣被鍨嬭浆鎹?,'high',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(4,1,'杩愮畻绗︼細绠楁湳銆佸叧绯汇€侀€昏緫銆佷綅杩愮畻','high',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(5,1,'娴佺▼鎺у埗璇彞锛歩f銆乻witch銆乫or銆亀hile','high',5,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(6,2,'绫讳笌瀵硅薄鐨勫畾涔夊拰鍒涘缓','high',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(7,2,'灏佽锛歱rivate/getter/setter','high',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(8,2,'缁ф壙锛歟xtends銆乻uper銆佹柟娉曢噸鍐?,'high',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(9,2,'澶氭€侊細缂栬瘧鏃朵笌杩愯鏃跺鎬?,'high',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(10,2,'鎺ュ彛涓庢娊璞＄被鐨勫尯鍒?,'medium',5,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(11,3,'寮傚父澶勭悊鏈哄埗锛歵ry-catch-finally','high',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(12,3,'鑷畾涔夊紓甯?,'medium',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(13,3,'String/StringBuilder/StringBuffer','high',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(14,3,'ArrayList涓嶩ashMap鍩烘湰浣跨敤','medium',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(15,4,'File绫诲父鐢ㄦ搷浣?,'medium',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(16,4,'瀛楄妭娴侊細FileInputStream/FileOutputStream','high',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(17,4,'瀛楃娴侊細FileReader/FileWriter','high',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(18,4,'缂撳啿娴侊細BufferedReader/BufferedWriter','medium',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(19,5,'椤哄簭琛ㄧ殑鎻掑叆涓庡垹闄ゆ搷浣?,'high',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(20,5,'鍗曢摼琛ㄧ殑寤虹珛涓庡熀鏈搷浣?,'high',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(21,5,'鏍堢殑鍏堣繘鍚庡嚭鐗规€т笌搴旂敤','high',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(22,5,'闃熷垪鐨勫厛杩涘厛鍑虹壒鎬т笌搴旂敤','high',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(23,6,'浜屽弶鏍戠殑鎬ц川涓庡瓨鍌ㄧ粨鏋?,'high',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(24,6,'鍓嶅簭/涓簭/鍚庡簭閬嶅巻','high',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(25,6,'鍝堝か鏇兼爲涓庡搱澶浖缂栫爜','medium',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(26,7,'鍥剧殑閭绘帴鐭╅樀涓庨偦鎺ヨ〃瀛樺偍','high',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(27,7,'娣卞害浼樺厛涓庡箍搴︿紭鍏堥亶鍘?,'high',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(28,7,'Prim涓嶬ruskal鏈€灏忕敓鎴愭爲绠楁硶','medium',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(29,7,'Dijkstra鏈€鐭矾寰勭畻娉?,'medium',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(30,8,'浜屽垎鏌ユ壘绠楁硶','high',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(31,8,'鍝堝笇琛ㄥ師鐞嗕笌鍐茬獊澶勭悊','medium',2,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(32,8,'鍐掓场鎺掑簭涓庡揩閫熸帓搴?,'high',3,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(33,8,'褰掑苟鎺掑簭绠楁硶','medium',4,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(34,9,'鍙橀噺銆佹暟鎹被鍨嬩笌杩愮畻绗?,'high',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(35,9,'鍒楄〃銆佸厓缁勩€佸瓧鍏镐笌闆嗗悎','high',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(36,9,'鏉′欢涓庡惊鐜鍙?,'high',3,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(37,10,'绫讳笌瀵硅薄鐨勫畾涔夊拰浣跨敤','high',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(38,10,'缁ф壙涓庡鎬?,'high',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(39,10,'瑁呴グ鍣ㄤ笌榄旀湳鏂规硶','medium',3,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(40,11,'鏂囦欢璇诲啓鎿嶄綔','high',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(41,11,'寮傚父鎹曡幏涓庤嚜瀹氫箟寮傚父','medium',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(42,12,'NumPy鏁扮粍鍩虹','medium',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(43,12,'Pandas鏁版嵁澶勭悊鍏ラ棬','medium',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(44,13,'杩涚▼涓庣嚎绋嬬殑姒傚康','high',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(45,13,'杩涚▼璋冨害绠楁硶','high',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(46,13,'绾跨▼鍚屾涓庝簰鏂?,'high',3,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(47,14,'鍒嗗尯涓庡垎椤靛瓨鍌ㄧ鐞?,'high',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(48,14,'铏氭嫙鍐呭瓨涓庨〉闈㈢疆鎹㈢畻娉?,'high',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(49,15,'鏂囦欢鐩綍缁撴瀯','medium',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(50,15,'纾佺洏绌洪棿绠＄悊','medium',2,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(51,16,'I/O鎺у埗鏂瑰紡','medium',1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(52,16,'SPOOLing鎶€鏈?,'medium',2,'2026-07-09 14:34:43','2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_knowledge_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_material`
--

DROP TABLE IF EXISTS `tb_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '璧勬枡ID',
  `course_id` bigint NOT NULL COMMENT '鎵€灞炶绋婭D',
  `teacher_id` bigint NOT NULL COMMENT '鍙戝竷鏁欏笀ID',
  `title` varchar(200) NOT NULL COMMENT '璧勬枡鏍囬',
  `content` text COMMENT '璧勬枡鍐呭',
  `required_seconds` int DEFAULT '60' COMMENT '鏈€浣庨槄璇绘椂闀?绉?',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍙戝竷鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼璧勬枡琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_material`
--

LOCK TABLES `tb_material` WRITE;
/*!40000 ALTER TABLE `tb_material` DISABLE KEYS */;
INSERT INTO `tb_material` VALUES (1,1,2,'Java绗竴绔犺涔?,'Java鏄竴绉嶉潰鍚戝璞＄殑缂栫▼璇█锛岀敱Sun Microsystems鍏徃浜?995骞存帹鍑恒€俓n\n鐗圭偣锛歕n1. 璺ㄥ钩鍙版€n2. 闈㈠悜瀵硅薄\n3. 瀹夊叏鎬n4. 澶氱嚎绋媆n\n鍩烘湰鏁版嵁绫诲瀷鍖呮嫭锛歩nt銆乨ouble銆乥oolean銆乧har绛夈€?,120,'2026-07-09 14:22:00'),(2,1,2,'Java绗簩绔犺涔?,'闈㈠悜瀵硅薄缂栫▼涓夊ぇ鐗瑰緛锛氬皝瑁呫€佺户鎵裤€佸鎬併€俓n\n灏佽锛氬皢鏁版嵁鍜岃涓哄寘瑁呭湪涓€璧凤紝瀵瑰闅愯棌瀹炵幇缁嗚妭銆俓n缁ф壙锛氬瓙绫荤户鎵跨埗绫荤殑灞炴€у拰鏂规硶锛屽疄鐜颁唬鐮佸鐢ㄣ€俓n澶氭€侊細鍚屼竴鏂规硶鍦ㄤ笉鍚屽璞′笂鏈変笉鍚岃〃鐜般€?,180,'2026-07-09 14:22:00');
/*!40000 ALTER TABLE `tb_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_material_read`
--

DROP TABLE IF EXISTS `tb_material_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_material_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `material_id` bigint NOT NULL COMMENT '璧勬枡ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '寮€濮嬮槄璇绘椂闂?,
  `completed` tinyint DEFAULT '0' COMMENT '0-鏈畬鎴?1-宸插畬鎴?,
  `completed_time` datetime DEFAULT NULL COMMENT '瀹屾垚闃呰鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勬枡宸茶璁板綍琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_material_read`
--

LOCK TABLES `tb_material_read` WRITE;
/*!40000 ALTER TABLE `tb_material_read` DISABLE KEYS */;
INSERT INTO `tb_material_read` VALUES (1,1,1,'2026-04-01 10:00:00',1,'2026-04-01 10:02:30'),(2,2,1,'2026-04-05 14:00:00',0,NULL);
/*!40000 ALTER TABLE `tb_material_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_notification`
--

DROP TABLE IF EXISTS `tb_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '閫氱煡ID',
  `course_id` bigint NOT NULL COMMENT '鎵€灞炶绋婭D',
  `title` varchar(200) NOT NULL COMMENT '閫氱煡鏍囬',
  `content` text COMMENT '閫氱煡鍐呭',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫氱煡琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_notification`
--

LOCK TABLES `tb_notification` WRITE;
/*!40000 ALTER TABLE `tb_notification` DISABLE KEYS */;
INSERT INTO `tb_notification` VALUES (1,1,'璇剧▼寮€璇鹃€氱煡','璇剧▼銆怞ava绋嬪簭璁捐銆戝凡寮€璇撅紝璇峰悓瀛︿滑鎸夋椂鍙傚姞瀛︿範銆?,'2026-03-01 08:00:00'),(2,1,'鑰冭瘯鏃堕棿閫氱煡','璇剧▼銆怞ava绋嬪簭璁捐銆戣€冭瘯鏃堕棿宸茬‘瀹氫负 2026-06-20 14:00銆?,'2026-06-01 10:00:00'),(3,2,'璇剧▼寮€璇鹃€氱煡','璇剧▼銆愭暟鎹粨鏋勩€戝凡寮€璇撅紝璇峰悓瀛︿滑鎸夋椂鍙傚姞瀛︿範銆?,'2026-03-01 08:00:00'),(4,2,'鑰冭瘯鏃堕棿閫氱煡','璇剧▼銆愭暟鎹粨鏋勩€戣€冭瘯鏃堕棿宸茬‘瀹氫负 2026-06-25 14:00銆?,'2026-06-01 10:00:00'),(5,4,'Python璇剧▼寮€璇鹃€氱煡','璇剧▼銆怭ython绋嬪簭璁捐銆戝凡寮€璇撅紝璇峰悓瀛︿滑鎸夋椂鍙傚姞瀛︿範銆?,'2026-06-20 08:00:00'),(6,5,'鎿嶄綔绯荤粺璇剧▼寮€璇鹃€氱煡','璇剧▼銆愭搷浣滅郴缁熷師鐞嗐€戝凡寮€璇撅紝璇峰悓瀛︿滑鎸夋椂鍙傚姞瀛︿範銆?,'2026-06-21 08:00:00'),(7,4,'绗竴娆′綔涓氬彂甯?,'Python鍩虹缂栫▼缁冧範宸插彂甯冿紝鎴鏃ユ湡锛?026-07-20 23:59:59','2026-07-01 10:00:00');
/*!40000 ALTER TABLE `tb_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_notification_read`
--

DROP TABLE IF EXISTS `tb_notification_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_notification_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '宸茶璁板綍ID',
  `notification_id` bigint NOT NULL COMMENT '閫氱煡ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '宸茶鏃堕棿',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='閫氱煡宸茶璁板綍琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_notification_read`
--

LOCK TABLES `tb_notification_read` WRITE;
/*!40000 ALTER TABLE `tb_notification_read` DISABLE KEYS */;
INSERT INTO `tb_notification_read` VALUES (1,1,1,'2026-03-01 08:30:00'),(2,2,1,'2026-06-01 10:30:00'),(3,3,1,'2026-03-01 08:30:00'),(4,4,1,'2026-06-01 10:30:00');
/*!40000 ALTER TABLE `tb_notification_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_resubmit_opportunity`
--

DROP TABLE IF EXISTS `tb_resubmit_opportunity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_resubmit_opportunity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignment_id` bigint NOT NULL COMMENT '浣滀笟ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `deadline` datetime NOT NULL COMMENT '琛ヤ氦鎴鏃堕棿',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assign_stu` (`assignment_id`,`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='琛ヤ氦鏈轰細琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_resubmit_opportunity`
--

LOCK TABLES `tb_resubmit_opportunity` WRITE;
/*!40000 ALTER TABLE `tb_resubmit_opportunity` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_resubmit_opportunity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_submission`
--

DROP TABLE IF EXISTS `tb_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignment_id` bigint NOT NULL COMMENT '浣滀笟ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `content` text COMMENT '鎻愪氦鍐呭',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '闄勪欢鍦板潃',
  `score` int DEFAULT NULL COMMENT '寰楀垎',
  `comment` varchar(500) DEFAULT NULL COMMENT '鏁欏笀璇勮',
  `status` tinyint DEFAULT '0' COMMENT '0-鏈彁浜?1-宸叉彁浜?2-宸叉壒鏀?,
  `submit_time` datetime DEFAULT NULL COMMENT '鎻愪氦鏃堕棿',
  `grade_time` datetime DEFAULT NULL COMMENT '鎵规敼鏃堕棿',
  `submit_count` int DEFAULT '0' COMMENT '绗嚑娆℃彁浜?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assign_stu` (`assignment_id`,`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣滀笟鎻愪氦琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_submission`
--

LOCK TABLES `tb_submission` WRITE;
/*!40000 ALTER TABLE `tb_submission` DISABLE KEYS */;
INSERT INTO `tb_submission` VALUES (1,1,1,'public class StudentManager { private String name; public StudentManager(String name) { this.name = name; } public static void main(String[] args) { System.out.println(\"Hello\"); } }',NULL,NULL,NULL,1,'2026-04-10 10:30:00',NULL,0),(2,4,5,'def calc_score():\n    scores = list(map(int, input(\"璇疯緭鍏ユ垚缁?绌烘牸鍒嗛殧): \").split()))\n    print(f\"鏈€楂樺垎: {max(scores)}, 鏈€浣庡垎: {min(scores)}, 骞冲潎鍒? {sum(scores)/len(scores):.2f}\")\n    pass_cnt = sum(1 for s in scores if s >= 60)\n    print(f\"鍙婃牸浜烘暟: {pass_cnt}, 涓嶅強鏍间汉鏁? {len(scores)-pass_cnt}\")\n\nif __name__ == \"__main__\":\n    calc_score()',NULL,NULL,NULL,1,'2026-07-15 14:30:00',NULL,1),(3,5,6,'import pandas as pd\ndf = pd.read_csv(\"students.csv\")\n# 鏁版嵁娓呮礂\ndf = df.dropna()\ndf = df[df[\"score\"] >= 0]\n# 缁熻鍒嗘瀽\nprint(df.describe())\n# 鍙鍖朶nimport matplotlib.pyplot as plt\ndf[\"score\"].hist()\nplt.savefig(\"score_dist.png\")',NULL,NULL,NULL,1,'2026-07-28 16:00:00',NULL,1),(4,6,5,'class Scheduler:\n    def fcfs(self, processes):\n        # 鍏堟潵鍏堟湇鍔n        processes.sort(key=lambda p: p[\"arrival\"])\n        time = 0\n        for p in processes:\n            if time < p[\"arrival\"]:\n                time = p[\"arrival\"]\n            time += p[\"burst\"]\n            p[\"completion\"] = time\n        return processes',NULL,NULL,NULL,1,'2026-07-20 10:00:00',NULL,1);
/*!40000 ALTER TABLE `tb_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user`
--

DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '鐢ㄦ埛鍚?,
  `password` varchar(100) NOT NULL COMMENT '瀵嗙爜',
  `real_name` varchar(50) DEFAULT NULL COMMENT '鐪熷疄濮撳悕',
  `role` tinyint NOT NULL DEFAULT '1' COMMENT '1-瀛︾敓 2-鏁欏笀 3-绠＄悊鍛?,
  `email` varchar(100) DEFAULT NULL COMMENT '閭',
  `phone` varchar(20) DEFAULT NULL COMMENT '鎵嬫満鍙?,
  `title` varchar(50) DEFAULT NULL COMMENT '鑱岀О 濡傛暀鎺?鍓暀鎺?璁插笀',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '0-绂佺敤 1-鍚敤',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user`
--

LOCK TABLES `tb_user` WRITE;
/*!40000 ALTER TABLE `tb_user` DISABLE KEYS */;
INSERT INTO `tb_user` VALUES (1,'zhangsan','123456','寮犱笁',1,NULL,NULL,NULL,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(2,'lisi','123456','鏉庡洓',2,'lisi@edu.cn','13800138000','鏁欐巿',1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(3,'wangwu','123456','鐜嬩簲',3,NULL,NULL,NULL,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(4,'zhaosi','123456','璧靛洓',1,NULL,NULL,NULL,1,'2026-07-09 14:22:00','2026-07-09 14:22:00'),(5,'liuxiao','e10adc3949ba59abbe56e057f20f883e','鍒樻檽',1,'liuxiao@edu.com','13800000005',NULL,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(6,'chenming','e10adc3949ba59abbe56e057f20f883e','闄堟槑',1,'chenming@edu.com','13800000006',NULL,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(7,'sunli','e10adc3949ba59abbe56e057f20f883e','瀛欎附',2,'sunli@edu.com','13900000007','鍓暀鎺?,1,'2026-07-09 14:34:43','2026-07-09 14:34:43'),(8,'zhouqiang','e10adc3949ba59abbe56e057f20f883e','鍛ㄥ己',1,'zhouqiang@edu.com','13800000008',NULL,1,'2026-07-09 14:34:43','2026-07-09 14:34:43');
/*!40000 ALTER TABLE `tb_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'edu'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-09 14:37:50
