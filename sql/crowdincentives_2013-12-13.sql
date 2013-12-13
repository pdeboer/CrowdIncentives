# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.28)
# Database: crowdincentives
# Generation Time: 2013-12-13 21:45:27 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table codes
# ------------------------------------------------------------

DROP TABLE IF EXISTS `codes`;

CREATE TABLE `codes` (
  `code` varchar(300) NOT NULL,
  `round_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `codes` WRITE;
/*!40000 ALTER TABLE `codes` DISABLE KEYS */;

INSERT INTO `codes` (`code`, `round_id`)
VALUES
	('secret',1),
	('patrick',1),
	('erik',1);

/*!40000 ALTER TABLE `codes` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table global
# ------------------------------------------------------------

DROP TABLE IF EXISTS `global`;

CREATE TABLE `global` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `round_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(1024) NOT NULL DEFAULT '',
  `create_date` datetime NOT NULL,
  `last_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `global` WRITE;
/*!40000 ALTER TABLE `global` DISABLE KEYS */;

INSERT INTO `global` (`id`, `round_id`, `user_id`, `name`, `create_date`, `last_modification`)
VALUES
	(1,1,1,'sdfghjkhbnj 2','2013-12-12 16:13:07','2013-12-12 16:40:50'),
	(2,1,1,'sdfghjkhbnj','2013-12-12 16:13:36','2013-12-12 16:47:22'),
	(3,1,1,'asdgasdg','2013-12-12 16:23:52','2013-12-12 16:39:25'),
	(4,1,6,'blablupp','2013-12-12 17:39:32','2013-12-12 21:26:44'),
	(5,1,7,'yay story','2013-12-13 15:02:04','2013-12-13 15:02:23');

/*!40000 ALTER TABLE `global` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table global_parts
# ------------------------------------------------------------

DROP TABLE IF EXISTS `global_parts`;

CREATE TABLE `global_parts` (
  `global_id` int(11) unsigned NOT NULL,
  `part_id` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`global_id`,`part_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `global_parts` WRITE;
/*!40000 ALTER TABLE `global_parts` DISABLE KEYS */;

INSERT INTO `global_parts` (`global_id`, `part_id`)
VALUES
	(4,1),
	(4,4),
	(4,5),
	(5,2),
	(5,6);

/*!40000 ALTER TABLE `global_parts` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table part
# ------------------------------------------------------------

DROP TABLE IF EXISTS `part`;

CREATE TABLE `part` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `round_id` int(11) NOT NULL,
  `template_part_id` int(11) NOT NULL,
  `name` varchar(1024) NOT NULL,
  `body` text NOT NULL,
  `create_date` datetime NOT NULL,
  `last_modification` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `part` WRITE;
/*!40000 ALTER TABLE `part` DISABLE KEYS */;

INSERT INTO `part` (`id`, `user_id`, `round_id`, `template_part_id`, `name`, `body`, `create_date`, `last_modification`)
VALUES
	(1,1,1,1,'test','asdfasdfasdf\r\na sdf\r\na\r\nsdf \r\nasd\r\nf\r\na\r\ns\r\ndfasdf','2013-12-11 18:23:13','2013-12-11 18:23:13'),
	(2,1,1,2,'asdfasdf','asdgasdgasdg','2013-12-11 18:49:11','2013-12-11 18:49:11'),
	(3,1,1,1,'my super great story','asdfasdgfasg\r\nasdg\r\nas\r\ndg\r\na\r\n\r\n\r\n\r\n\r\nasdgasdgasdgag\r\nasd\r\nga\r\nsdg\r\nasd\r\ngasd\r\ngasdgasdgasdg\r\n\r\n\r\n\r\nasdfasdfadfasdfasdf','2013-12-12 14:34:22','2013-12-12 14:34:22'),
	(4,1,1,2,'another part 2','another part 2\r\n\r\n\r\nwith multiple lines','2013-12-12 16:01:35','2013-12-12 16:01:35'),
	(5,1,1,3,'oooh part 3','blabla','2013-12-12 16:01:44','2013-12-12 16:01:44'),
	(6,6,1,1,'test before 1','asdgasdgasdga','2013-12-12 17:39:20','2013-12-12 17:39:20'),
	(7,7,1,1,'our great part 1 story','whatever 1\r\n\r\n\r\n\r\nasdfasdf\r\n\r\n\r\n\r\n\r\nasdgasdgasg','2013-12-13 15:00:54','2013-12-13 15:00:54');

/*!40000 ALTER TABLE `part` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table round
# ------------------------------------------------------------

DROP TABLE IF EXISTS `round`;

CREATE TABLE `round` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `template_id` int(11) NOT NULL,
  `description` varchar(800) DEFAULT NULL,
  `fromTime` datetime NOT NULL,
  `toTime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `round` WRITE;
/*!40000 ALTER TABLE `round` DISABLE KEYS */;

INSERT INTO `round` (`id`, `template_id`, `description`, `fromTime`, `toTime`)
VALUES
	(1,1,'test round 1','2013-12-13 16:55:09','2013-12-13 17:50:09');

/*!40000 ALTER TABLE `round` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table template
# ------------------------------------------------------------

DROP TABLE IF EXISTS `template`;

CREATE TABLE `template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(600) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;

INSERT INTO `template` (`id`, `name`)
VALUES
	(1,'test');

/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table template_part
# ------------------------------------------------------------

DROP TABLE IF EXISTS `template_part`;

CREATE TABLE `template_part` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `template_id` int(11) NOT NULL,
  `before_text` text,
  `description` text,
  `after_text` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `template_part` WRITE;
/*!40000 ALTER TABLE `template_part` DISABLE KEYS */;

INSERT INTO `template_part` (`id`, `template_id`, `before_text`, `description`, `after_text`)
VALUES
	(1,1,'before 1\n','Test Part 1','Test after text'),
	(2,1,'before 2\n','Test Part 2','Test after text 2'),
	(3,1,'before 3\n','Test Part 3','Test after text 3');

/*!40000 ALTER TABLE `template_part` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(200) NOT NULL DEFAULT '',
  `password` varchar(200) NOT NULL DEFAULT '',
  `round` int(11) NOT NULL,
  `code` varchar(300) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` (`id`, `username`, `password`, `round`, `code`)
VALUES
	(1,'asdf','asdf',1,''),
	(6,'testuser2','asdfasdf',1,'secret'),
	(7,'erik1','asdf',1,'erik');

/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
