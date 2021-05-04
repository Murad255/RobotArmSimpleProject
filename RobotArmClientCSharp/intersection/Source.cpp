#include <iostream>
#include <conio.h>
#include <stdio.h>
#include <string.h>

using namespace::std;

void main() {
	bool pinFlag = 1;
	int UID = 146;

	char str[] = "<Modul>\
	<Name>but1</Name>\
	<ModuleType>2</ModuleType>\
	<UID>146</UID>\
	<Data>1</Data>\
	<UID>111</UID>\
	<Status>1</Status>\
	</Modul>";

	char* pch;
	printf("Splitting string \"%s\" into tokens:\n", str);
	pch = strchr(str, "<Name>");
	while (pch != NULL)
	{
		printf("%s\n", pch);
		pch = strtok(NULL, " ,.-");
	}


	return;
}