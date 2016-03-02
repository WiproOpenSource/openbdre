#!/bin/bash
rm cookies.txt
base_url=$1
username=$2
password=$3
check_string=$4

curl -L -c cookies.txt -b cookies.txt "$base_url/auth/bdre/security/login.page" -H "Pragma: no-cache" -H "Accept-Encoding: gzip, deflate, sdch" -H "Accept-Language: en-US,en;q=0.8" -H "Upgrade-Insecure-Requests: 1" -H "User-Agent: Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36" -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" -H "Cache-Control: no-cache" -H "Connection: keep-alive" --compressed > /dev/null 2>&1

export metaCount=`curl -L -c cookies.txt -b cookies.txt "$base_url/auth/j_spring_security_check" -H "Pragma: no-cache" -H "Origin: $base_url" -H "Accept-Encoding: gzip, deflate" -H "Accept-Language: en-US,en;q=0.8" -H "Upgrade-Insecure-Requests: 1" -H "User-Agent: Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36" -H "Content-Type: application/x-www-form-urlencoded" -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8" -H "Cache-Control: no-cache" -H "Referer: $base_url/auth/bdre/security/login.page" -H "Connection: keep-alive" --data "username=$username&password=$password" --compressed 2>/dev/null | grep "$check_string" | wc -l`

echo metaCount = $metaCount

if [ "$metaCount" == "1" ] 
then
	echo "Login successful"
else
	echo "Login failed"
	exit 1
fi

