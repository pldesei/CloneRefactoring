#!/bin/bash

projects=(lucene freecol jfreechart hibernate elasticsearch axis2-java eclipse.jdt.core)
cnt=1
for name in ${projects[@]}
do
    cnt=$((cnt+1))
    echo $name
    list=`ls $name/0.4`
    listAinB=`head -n $cnt | tail -n 1 | awk '
       {
         len1=split($2, arr1, ","); 
         len2=split("'$list'", arr2, "refactor");
         for (i=0;i<len1;i++) {
             flag=0;
             for (j=0;j<len2;j++)
                 if (arr1[i] == arr2[j]) flag = 1;
             if (flag == 1) print arr1[i]; 
         }
       }'`
    list_AinB=`head -n $cnt | tail -n 1 | awk '
       {
         len1=split($3, arr1, ","); 
         len2=split("'$list'", arr2, "refactor");
         for (i=0;i<len1;i++) {
             flag=0;
             for (j=0;j<len2;j++)
                 if (arr1[i] == arr2[j]) flag = 1;
             if (flag == 1) print arr1[i]; 
         }
       }'`
    echo "list A in B : $listAinB \n"
    echo "list _A in B : $list_AinB \n"

done

