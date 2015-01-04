double* quickSort(double* numbers, int nsize, int level, MPI_Comm comm, int splitid, int* final_size){
	
	int npes, myid, npesSplit;
	MPI_Comm_size(MPI_COMM_WORLD, &npes);
	MPI_Comm_size(comm, &npesSplit);
	MPI_Comm_rank(MPI_COMM_WORLD, &myid);
	int dimensions = log10(npes)/log10(2);
	
	std::sort(numbers, numbers + nsize); //local sort

	//base case
	if(level == dimensions){
		return numbers;
	}
	
	double median = numbers[nsize/2]; //requires cpp's integer division

	double* sendBuf;
	sendBuf = (double*)malloc(1*sizeof(double));
	sendBuf[0] = median;
	
	int splitDelin = 1;
	if(level >= 1){
		splitDelin = 2;
	}
	int procGroup = npesSplit / splitDelin; //number of proc per group is half of every previous level
	
	double* recvBuf = new double[procGroup]; //size only needs to be size of new comm	
	
	MPI_Comm splitcomm = comm;
	int splitrank = myid;
	if(level > 0){
		int color = (splitid >= 0 && splitid <= (procGroup - 1)) ? 1 : 2;
		MPI_Comm_split(comm,color,splitid,&splitcomm); //works on first level?
		MPI_Comm_rank(splitcomm, &splitid);
	}
		
	MPI_Allgather(sendBuf, 1, MPI_DOUBLE, recvBuf, 1, MPI_DOUBLE, splitcomm); //splitcomm

	//sort only the correct number of processors
	std::sort(recvBuf, recvBuf + procGroup); //sort medians and pick middle
	double pivot = recvBuf[procGroup/2];

	//split numbers based on pivot. 
	int count = 0, splitInd = nsize;
	for(int i = 0; i < nsize; i++){
		if(numbers[i] >= pivot){
			splitInd = i;
			break;
		}
		count++;
	}
	int highsize = nsize - count; //number of values above pivot
	int lowsize = nsize - highsize; //number of values below pivot


	double* lowArray = new double[lowsize];
	double* highArray = new double[highsize];
	int highInd = 0;
	
	
	for(int i = 0; i < nsize; i++){
		if(i < count){
			lowArray[i] = numbers[i];
		}
		else{
			highArray[highInd++] = numbers[i];
		}
	}
	
	//step 4 here, find partner process by XORing binary string correct bit
	int* binString = new int[dimensions];
	binString = toBinary(myid, dimensions);	
	binString[level] = binString[level] ^ 1;
	int partner = toInt(binString, dimensions);
	delete [] binString;
	int sendSize[1];
	int recvSize[1];
	
	if(myid > partner){
		sendSize[0] = lowsize;
	}
	else{
		sendSize[0] = highsize;
	}
	MPI_Status sizeStatus;
	//determine size
	MPI_Sendrecv(sendSize, 1, MPI_DOUBLE, partner, 42,
			recvSize, 1, MPI_DOUBLE, partner, 42, MPI_COMM_WORLD, &sizeStatus);
	
	double* dataBuf = new double[recvSize[0]];
	
	MPI_Status status;
	if(myid > partner){ //send low array	

		MPI_Sendrecv(lowArray, lowsize, MPI_DOUBLE, partner, 42,
			dataBuf, recvSize[0], MPI_DOUBLE, partner, 42, MPI_COMM_WORLD, &status);
	}
	else{ //send high array

		MPI_Sendrecv(highArray, highsize, MPI_DOUBLE, partner, 42,
			dataBuf, recvSize[0], MPI_DOUBLE, partner, 42, MPI_COMM_WORLD, &status);
	}
 
	
	//then combine the two arrays and recurse
	int newsize = 0;
	int switchpoint = 0;
	char here = 'a';
	if(myid > partner){
		newsize = highsize + recvSize[0];
		switchpoint = highsize;
		here = 'h';
	}
	else{
		newsize = lowsize + recvSize[0];
		switchpoint = lowsize;
		here = 'l';
	}
	
	//put both arrays in newNumbers
	double* newNumbers = new double[newsize]; //holds the values
	int index = 0;
	for(int i = 0; i < newsize; i++){
		if(here == 'l'){
			if(i < switchpoint){
				newNumbers[i] = lowArray[i];
			}
			else{
				newNumbers[i] = dataBuf[index++];
		}
			}
		else if(here == 'h'){
			if(i < switchpoint){
				newNumbers[i] = highArray[i];
			}
			else{
				newNumbers[i] = dataBuf[index++];
			}
		}
		else{
			cout<<endl<<endl<<"q#%&^q#(%*^q#)%((";
		}
		
	}
	
	free(highArray);
	free(lowArray);	
	*final_size = newsize;
	numbers = quickSort(newNumbers, newsize, level + 1, splitcomm, splitid, final_size);

	free(sendBuf);
	free(recvBuf);
	return numbers;
}