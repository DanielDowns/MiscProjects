<?php

	//host src on github and point link there

	include 'functions.php';
	
	$domain = "http://bserver-blizzardtest.rhcloud.com/";
	
	define('DB_HOST', getenv('OPENSHIFT_MYSQL_DB_HOST'));
	define('DB_PORT', getenv('OPENSHIFT_MYSQL_DB_PORT')); 
	define('DB_USER', getenv('OPENSHIFT_MYSQL_DB_USERNAME'));
	define('DB_PASS', getenv('OPENSHIFT_MYSQL_DB_PASSWORD'));
	define('DB_NAME', getenv('OPENSHIFT_GEAR_NAME'));
	
	
	//get url
	$protocol = $_SERVER['HTTPS'] == 'on' ? 'https' : 'http';
	$location = $_SERVER['REQUEST_URI'];
	if ($_SERVER['QUERY_STRING']) {
	  $location = substr($location, 0, strrpos($location, $_SERVER['QUERY_STRING']) - 1);
	}
	$url = $protocol.'://'.$_SERVER['HTTP_HOST'].$location;
	if(substr($url, -1) != '/'){ //ensure consistency in trailing /
		$url = $url.'/';
	}
	
	//get parameters
	$length = strlen($domain); 
	$parameters = substr($url, $length);
	
	$parameterArray = explode('/', $parameters); //to account for null para due to trailing /
	$parameterCount = count($parameterArray) - 1;
	
	$type = $_SERVER['REQUEST_METHOD'];

	switch($type){
		case 'GET':
			processGET($parameterCount, $parameterArray);	
			break;
		case 'POST':
			processPOST($parameterCount, $parameterArray);
			break;	
		case 'DELETE':
			processDELETE($parameterCount, $parameterArray);
			break;
		default:
			throwError();
			break;
	} 
?>
