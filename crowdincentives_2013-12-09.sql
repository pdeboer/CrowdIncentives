# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.28)
# Database: crowdincentives
# Generation Time: 2013-12-10 01:34:30 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table global
# ------------------------------------------------------------

DROP TABLE IF EXISTS `global`;

CREATE TABLE `global` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `round_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(1024) NOT NULL DEFAULT '',
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table part
# ------------------------------------------------------------

DROP TABLE IF EXISTS `part`;

CREATE TABLE `part` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `round_id` int(11) NOT NULL,
  `template_part_id` int(11) NOT NULL,
  `create_date` datetime NOT NULL,
  `body` text NOT NULL,
  `name` varchar(1024) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table round
# ------------------------------------------------------------

DROP TABLE IF EXISTS `round`;

CREATE TABLE `round` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `template_id` int(11) NOT NULL,
  `description` varchar(800) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table template
# ------------------------------------------------------------

DROP TABLE IF EXISTS `template`;

CREATE TABLE `template` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(600) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



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



# Dump of table users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(200) NOT NULL DEFAULT '',
  `password` varchar(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` (`id`, `username`, `password`)
VALUES
	(1,'asdf','asdf');

/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
