

#include<stdio.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>

int main()
{
	int fp,i;
	int count=0;
	char ch;
	printf("\n starting the packet loss check");
	fp=open("cat1.txt",O_RDONLY,777);
	if(fp<0)
	{
		printf("\n Error in Opening the file");
		return -1;
	}

	//first count the number of words present in the file
	printf("\n starting the count");
	for(i=0;ch!='\0';i++)	
	{
		read(fp,&ch,1);
		if(ch!='\0')
			count++;
	}
	
	printf("\n Number of words :%d",count);




	
	return 0;
}
