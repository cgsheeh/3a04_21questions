<?php 
/**
My Crypt class for encrypting and decrpyting the messages sent via android
**/
class MCrypt
{
    //private iv and key to encrypt/decrypt
    private $iv = 'afrosamuraicool1'; #Same as in JAVA
    private $key = 'diodebjtmosfet12'; #Same as in JAVA

/**
Contructor for Class
**/
    function __construct()
    {
    }

/**
encrypt
	Takes in a string and will return the encrypted it with the iv and key to send
@param $str The string that will be encrypted
@return The encrypted information to send
**/
    function encrypt($str) {

      //$key = $this->hex2bin($key);    
      $iv = $this->iv;

      $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

      mcrypt_generic_init($td, $this->key, $iv);
      $encrypted = mcrypt_generic($td, $str);

      mcrypt_generic_deinit($td);
      mcrypt_module_close($td);

      return bin2hex($encrypted);
    }

/**
decrypt
	Takes an encrypted string and will decrypted it with the iv and key
@param $code the encoded message to decrypt
@return The readble message
**/
    function decrypt($code) {
      //$key = $this->hex2bin($key);
      $code = $this->hex2bin($code);
      $iv = $this->iv;
	
      $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);
	
      mcrypt_generic_init($td, $this->key, $iv);
      	
	$decrypted = mdecrypt_generic($td, $code);
      mcrypt_generic_deinit($td);
      mcrypt_module_close($td);
      return utf8_encode(trim($decrypted));
    }

    protected function hex2bin($hexdata) {
      $bindata = '';

      for ($i = 0; $i < strlen($hexdata); $i += 2) {
        $bindata .= chr(hexdec(substr($hexdata, $i, 2)));
      }

      return $bindata;
    }

}
?>
