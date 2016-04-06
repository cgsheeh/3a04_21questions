<?php
//error_reporting(E_ALL);
//ini_set('display_errors', 'On');
//var_dump(function_exists('utf8_decode'));
/*
 * Following code will list the values from the query and encrypt
 * For the message the android application
 */

// array for JSON response
$response = array();
// include db connect class
require_once __DIR__ . '/db_connect.php';
require_once __DIR__ . '/mcrypt.php'; 

$db = new DB_CONNECT();
$mcrypt = new MCrypt();

//get the encrypted query and use the mycrypt libary to unencrypt it
$encrypted_data = ($_REQUEST["query"]);

$query = $mcrypt->decrypt($encrypted_data);

//run the query
$result = mysql_query($query) or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    /*
	Take all the results and encrypt them and display them.
    */
    $response["responce"] = array();

    $product = array();
    $data = "";
    while ($row = mysql_fetch_array($result)) {
        
	
	for ($i =0; $i < count($row)/2 ; $i = $i+1){
	    	$data .=  $row[$i];	
		if($i < count($row)/2 -1){
			$data .= ",";
		}
	}
	$data .="\n";

    }
     
    	// successful;
	echo $mcrypt->encrypt($data);
} else {
    // no products found
    
     echo $mcrypt->encrypt("null"); //json_encode($response);

}
?>
