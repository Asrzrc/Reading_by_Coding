#include <stdio.h>

void squeeze(char s1[], char s2[]){
    int i, j, k;

    for(i = k = 0; s1[i] != '\0'; i++){
        for(j = 0; s2[j] != '\0' && s2[j] != s1[i]; j++)
            ;
        if(s2[j] == '\0')
            s1[k++] = s1[i];
    }
    s1[k] = '\0';
}

int main(){
    char s2[] = "I'm your father.";
    char s1[] = "I'm yttss";
    squeeze(s1, s2);
    printf("%s:::%s\n", s1, s2);

    return 0;
}