<?php

	$domain = 'http://bserver-blizzardtest.rhcloud.com/';
	
	define('DB_HOST', getenv('OPENSHIFT_MYSQL_DB_HOST'));
	define('DB_PORT', getenv('OPENSHIFT_MYSQL_DB_PORT')); 
	define('DB_USER', getenv('OPENSHIFT_MYSQL_DB_USERNAME'));
	define('DB_PASS', getenv('OPENSHIFT_MYSQL_DB_PASSWORD'));
	define('DB_NAME', getenv('OPENSHIFT_GEAR_NAME'));

	//called when something is wrong with a parameter
	function invalidRequest(){
		header("HTTP/1.0 403 Forbidden");
	}
	
	//about the server
	function getAbout(){
		$response = '<?xml version="1.0" encoding="utf-8"?>';
		$response = $response.'<data><name>Daniel Downs</name>';
		$response = $response.'<src>http://bserver-blizzardtest.rhcloud.com/src/</src></data>';
		echo $response;
	}
	
	//create a new account
	function createAccount(){
		$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
		try{
			$name = $_POST["name"];
			$id = strval(rand(1, 100000)); 
		
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "INSERT INTO account (account_name, id) VALUES (:account_name, :id)";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(':account_name'=>$name, ':id'=>$id));
			
			$response = '<?xml version="1.0" encoding="utf-8"?>';
			$response = $response.'<account_id>'.$id.'</account_id>';
			echo $response;
		}
		catch(PDOException $ex){
			invalidRequest();
		}
	} 
	
	//create a new character
	function createCharacter($accountName){
		$charName = $_POST['name'];
		$charRace = $_POST['race'];
		$charClass = $_POST['class'];
		$charFaction = $_POST['faction'];
		$charLevel = $_POST['level'];
		
		$charName = str_replace(' ',"_",$charName);
		
		$charID = strval(rand(1, 100000)); //generate id here
		$deleted = "false";
		
		//check for a viable construction
		$validBuild = true;
		
		//null parameter check
		if(is_null($charName) or is_null($charRace) or is_null($charClass) 
		or is_null($charFaction) or is_null($charLevel)){
			$validBuild = false;
		}
		
		//valid options check
		if($charRace != "Orc" and $charRace != "Tauren" and $charRace != "Blood Elf" 
			and $charRace != "Human" and $charRace != "Gnome" and $charRace != "Worgen"){
			$validBuild = false;
		}
		if($charClass != "Warrior" and $charClass != "Druid" 
			and $charClass != "Death Knight" and $charClass != "Mage"){
			$validBuild = false;
		}	
		if($charFaction != "Horde" and $charFaction != "Alliance"){
			$validBuild = false;
		}
		if($charLevel < 1 or $charLevel > 85){
			$validBuild = false;
		}
		
		//faction check
		if(($charRace == "Orc" or $charRace == "Tauren" or $charRace == "Blood Elf") 
		and $charFaction == "Alliance"){
			$validBuild = false;
		}
		if(($charRace == "Human" or $charRace == "Gnome" or $charRace == "Worgen") 
		and $charFaction == "Horde"){
			$validBuild = false;
		}
		
		//class check
		if($charRace == "Blood Elf" and $charClass == "Warrior"){
			$validBuild = false;
		}
		if(($charRace == "Human" or $charRace == "Gnome" or $charRace == "Orc" or $charRace == "Blood Elf") 
		and $charClass == "Druid"){
			$validBuild = false;
		}
	
		//check account to make sure no other-faction characters
		try{
			$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "SELECT char_faction, deleted FROM character_data WHERE account_name=(:account_name)";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(':account_name'=>$accountName));
			$result = $q->fetchAll();
						
		}
		catch(PDOException $ex){
			invalidRequest();
		}
		
		//can have different faction characters but only if one faction is deleted
		foreach($result as $row){
			if(($row['char_faction'] != $charFaction) and ($row['deleted'] != 'true')){
				$validBuild = false;
				break;
			}
		}
		
		//check to see if viable account exists
		if(count($result) == 0){
			try{
				$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
				$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
				$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
				$sql = "SELECT * FROM account WHERE account_name=(:account_name)";
				$q = $dataHandle->prepare($sql);
				$q->execute(array(':account_name'=>$accountName));
				$result = $q->fetchAll();
						
			}
			catch(PDOException $ex){
				invalidRequest();
			}
			
			if(count($result) < 1){
				$validBuild = false;
			} 
		} 
	
		//create character
		if($validBuild == true){
			$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
			try{
			
				$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
				$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
				$sql = "INSERT INTO character_data (char_id, account_name, char_name, char_race, char_class, char_level, char_faction, deleted) 
					VALUES (:char_id, :account_name, :char_name, :char_race, :char_class, :char_level, :char_faction, :deleted)";
				$q = $dataHandle->prepare($sql);
				$q->execute(array(
					':char_id'=>$charID,
					':account_name'=>$accountName, 
					':char_name'=>$charName,
					':char_race'=>$charRace,
					':char_class'=>$charClass,
					':char_level'=>$charLevel,
					':char_faction'=>$charFaction,
					':deleted'=>$deleted
					));
			}
			catch(PDOException $ex){
				invalidRequest();
			}
			
			$response = '<?xml version="1.0" encoding="utf-8"?>';
			$response = $response.'<data><character_id>'.$charID.'</character_id></data>';
			echo $response;
		}
		else{
			invalidRequest();
		} 
		
	} 
	
	//delete account and all its characters
	function deleteAccount($accountName){
		$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
		try{
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "DELETE FROM account WHERE account_name = (:accountName)";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(':accountName'=>$accountName));
			
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "DELETE FROM character_data WHERE account_name = (:accountName)";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(':accountName'=>$accountName));
		}
		catch(PDOException $ex){
			invalidRequest();
		}
	} 
	
	//delete a character (doesn't remove from database)
	function deleteCharacter($accountName, $charName){
		$charName = str_replace("%20","_",$charName);
	
		try{
			$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "SELECT char_faction,deleted FROM character_data WHERE (char_name = (:char_name) AND account_name = (:account_name))";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(
			':char_name'=>$charName,
			':account_name'=>$accountName
			));
			$result = $q->fetchAll();
			
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "SELECT char_faction,deleted FROM character_data WHERE (char_name <> (:char_name) AND account_name = (:account_name))";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(
			':char_name'=>$charName,
			':account_name'=>$accountName
			));
			$altResult = $q->fetchAll(); 
		}
		catch(PDOException $ex){
			invalidRequest();
		}
		
		
		//determine whether to delete or undelete
		$undelete = "true";
		foreach($result as $target){
			$undelete = $target['deleted'];
			break;
		}
		
		
		$canUndelete = "true";
		//determine if you can undelete or there are alt-faction characters
		if($undelete == "true"){
			foreach($result as $target){
				foreach($altResult as $row){
					if(($target['char_faction'] != $row['char_faction']) and $row['deleted'] == 'false'){
						$canUndelete = "false";
						break;
					} 
				} 
				break;
			}
		}
		
		$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
		try{
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "UPDATE character_data SET deleted = 'true' WHERE (char_name = (:char_name) AND account_name = (:account_name))";
			if($undelete == "true" and $canUndelete == "true"){
				$sql = "UPDATE character_data SET deleted = 'false' WHERE (char_name = (:char_name) AND account_name = (:account_name))";
			}
			$q = $dataHandle->prepare($sql);
			$q->execute(array(
			':char_name'=>$charName,
			':account_name'=>$accountName
			));
		}
		catch(PDOException $ex){
			invalidRequest();
		}
	} 
	
	//return info about a specific account
	function returnAccountCharacters($accountName){
	
		try{
			$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "SELECT * FROM character_data WHERE account_name = (:accountName)";
			$q = $dataHandle->prepare($sql);
			$q->execute(array(':accountName'=>$accountName));
			$result = $q->fetchAll();
			
		}
		catch(PDOException $ex){
			invalidRequest();
		}
		
		$response = '<?xml version="1.0" encoding="utf-8"?>';
		$response = $response.'<data>';
		foreach($result as $row){
		
			$response = $response.'<character>';
			
			$response = $response.'<character_id>'.$row['char_id'].'</character_id>';
			$modCharName = str_replace("_"," ",$row['char_name']);
			$response = $response.'<character_name>'.$modCharName.'</character_name>';
			$response = $response.'<race>'.$row['char_race'].'</race>';
			$response = $response.'<class>'.$row['char_class'].'</class>';
			$response = $response.'<level>'.$row['char_level'].'</level>';
			$response = $response.'<faction>'.$row['char_faction'].'</faction>';
			$response = $response.'<deleted>'.$row['deleted'].'</deleted>';
			
			$response = $response.'</character>';
		}
		$response = $response.'</data>';  
		echo $response;
	} 
	
	//returns info on all accounts
	function returnAllAccounts(){
		try{
			$dsn = 'mysql:dbname='.DB_NAME.';host='.DB_HOST.';port='.DB_PORT;
			$dataHandle = new PDO($dsn, DB_USER, DB_PASS);
			$dataHandle->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$sql = "SELECT * FROM account";
			$q = $dataHandle->prepare($sql);
			$q->execute();
			$result = $q->fetchAll();
		}
		catch(PDOException $ex){
			invalidRequest();
		}
		
		$response = '<?xml version="1.0" encoding="utf-8"?>';
		$response = $response.'<accounts>';
		foreach($result as $row){
			$response = $response.'<account>';
			$response = $response.'<account_id>'.$row['id'].'</account_id>';
			$response = $response.'<account_name>'.$row['account_name'].'</account_name>';
			$response = $response.'<link>http://bserver-blizzardtest.rhcloud.com/account/'.$row['account_name'].'/'.'</link>';
			$response = $response.'</account>';
		}
		$response = $response.'</accounts>'; 
		echo $response;
	}


	//deal with GET request
	function processGET($parameterCount, $parameterArray){
		if($parameterArray[0] == "about" and $parameterCount == 1){
			getAbout();
		}
		elseif($parameterArray[0] == "account" and $parameterCount == 1){
			returnAllAccounts();
		}
		elseif($parameterArray[0] == "account" and $parameterArray[2] == "characters" and $parameterCount == 3){
			returnAccountCharacters($parameterArray[1]);
		}
	}
	
	//deal with POST request
	function processPOST($parameterCount, $parameterArray){ 
		if($parameterArray[0] == "account" and $parameterCount == 1){
			createAccount();
		}
		elseif($parameterArray[0] == "account" and $parameterArray[2] == "characters" and $parameterCount == 3){
			createCharacter($parameterArray[1]);
		}
		
	}
	
	//deal with DELETE request
	function processDELETE($parameterCount, $parameterArray){
		if($parameterArray[0] == "account" and $parameterCount == 2){
			deleteAccount($parameterArray[1]);
		}
		elseif($parameterArray[0] == "account" and $parameterArray[2] == "characters" and $parameterCount == 4){
			deleteCharacter($parameterArray[1], $parameterArray[3]);
		}
	}
	
	//deal with unknown request
	function throwError(){
		invalidRequest();
	}
?>