
    
receiveMessage = '<Module>\
	<Name>RobotKuka</Name>\
	<ModuleType>1</ModuleType>\
	<UID>111</UID>\
	<Status>1</Status>\
	<Data>\
		<Task>\
			<Program>163</Program>\
		</Task>\
	</Data>\
</Module>'

KukaMessage =""

def  findText( str,  findContext):
	str.replace(' ', '')
	find1 = str.find('<' + findContext + '>')
	find2 = str.find('</' + findContext + '>')
	if(find1==-1) or (find2==-1):
		return ''
	findStr = '' 
	for  i in range( find1 + len('<' + findContext + '>'),  find2):
		findStr += str[i]
	return findStr

if receiveMessage.find('Module'):
	UID = findText(receiveMessage,"UID")
	Task = findText(receiveMessage,"Task")
	Name = findText(receiveMessage,"Name")
	if Name=="RobotKuka":
		KukaMessage+= 	"<Manipulator>\
							<UID>"+UID+"</UID>\
							<Task>"+Task+"</Task>\
							<Status>0</Status>\
						</Manipulator>"
		KukaMessage.replace(" ","")


#find = findText(text,"Pin16")


print(KukaMessage)

