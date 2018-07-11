awk 'BEGIN{found=0;}{if (found == 1) print $3, $4, $5; if ($0 ~ /Recall/) found = 1; else found = 0;}' OFS="\t" $1
