#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAT_SIZE 500

void brute_force_matmul(double* mat1, double *mat2, 
                        double res[MAT_SIZE][MAT_SIZE]) {
   /* matrix multiplication of mat1 and mat2, store the result in res */
    for (int i = 0; i < MAT_SIZE; ++i) {
        for (int j = 0; j < MAT_SIZE; ++j) {
            res[i][j] = 0;
            for (int k = 0; k < MAT_SIZE; ++k) {
                res[i][j] += mat1[i*MAT_SIZE+k] * mat2[k*MAT_SIZE+j];
            }
        }
    }
}

int main(int argc, char *argv[])
{
   int rank;
   int mpiSize;
   int namelen;
   char processor_name[MPI_MAX_PROCESSOR_NAME];
   //double a[MAT_SIZE][MAT_SIZE],    /* matrix A to be multiplied */
     //  b[MAT_SIZE][MAT_SIZE],       /* matrix B to be multiplied */
       //c[MAT_SIZE][MAT_SIZE],       /* result matrix C */
       double bfRes[MAT_SIZE][MAT_SIZE];   /* brute force result bfRes */

   

   /* You need to intialize MPI here */
   MPI_Comm comm = MPI_COMM_WORLD;
    MPI_Init(NULL, NULL);
    MPI_Comm_size(comm, &mpiSize);
    MPI_Comm_rank(comm, &rank);
    MPI_Get_processor_name(processor_name, &namelen);
    MPI_Status status;
    int local_size = MAT_SIZE/mpiSize;
    printf("Process %d on %s\n", rank, processor_name);
    double *a =(double*)malloc(MAT_SIZE*MAT_SIZE*sizeof(double));    
    double *b =(double*)malloc(MAT_SIZE*MAT_SIZE*sizeof(double));   
    double *local_matrix=(double*)malloc(local_size*MAT_SIZE*sizeof(double));   
    double *local_res=(double*)malloc(local_size*MAT_SIZE*sizeof(double));   
    double *c =(double*)malloc(MAT_SIZE*MAT_SIZE*sizeof(double)); 
    
   if (rank == 0)
   {
      /* master */

      /* First, fill some numbers into the matrix */
      srand(time(NULL));  
      for (int i = 0; i < MAT_SIZE; i++)
         for (int j = 0; j < MAT_SIZE; j++)
            a[i*MAT_SIZE+j] = rand() % 10;
      for (int i = 0; i < MAT_SIZE; i++)
         for (int j = 0; j < MAT_SIZE; j++)
            b[i*MAT_SIZE+j] = rand() % 10;

      /* Measure start time */
      double start = MPI_Wtime();
    
      /* Send matrix data to the worker tasks */
      MPI_Scatter(a, local_size*MAT_SIZE, MPI_DOUBLE, local_matrix, local_size*MAT_SIZE, MPI_DOUBLE, 0, comm);
      MPI_Bcast(b, MAT_SIZE*MAT_SIZE, MPI_DOUBLE, 0, comm);

      /* Receive results from worker tasks */
      for(int i=0;i<local_size;i++)
         for(int j=0;j<MAT_SIZE;j++){
         double tmp=0;
         for(int k=0;k<MAT_SIZE;k++)
            tmp += (local_matrix[i*MAT_SIZE+k] * b[k*MAT_SIZE+j]);
         local_res[i*MAT_SIZE+j]=tmp;
         }
      //free(local_matrix);
      MPI_Gather(local_res,local_size*MAT_SIZE,MPI_DOUBLE,c,local_size*MAT_SIZE,MPI_DOUBLE,0,comm);
        
      //Deal with undividable situation
      int rest=MAT_SIZE%mpiSize;
      if(rest!=0)
         for(int i=MAT_SIZE-rest-1;i<MAT_SIZE;i++)
         for(int j=0;j<MAT_SIZE;j++){
            double tmp=0;
            for(int k=0;k<MAT_SIZE;k++)
               tmp += a[i*MAT_SIZE+k]* b[k*MAT_SIZE+j];
            c[i*MAT_SIZE+j]=tmp;
         }  

      /* Measure finish time */
      double finish = MPI_Wtime();
      
      printf("Done in %f seconds.\n", finish - start);

      printf("%s","---------------THIS IS THE RESULT OF PARALLELD COMPUTING:-----------------\n");
      /*
       for (int i=0;i<MAT_SIZE;i++){
         for(int j=0;j<MAT_SIZE;j++){
            printf("%.1f ", c[i*MAT_SIZE+j]);

         }
         printf("\n");
      }
      */
     
      /* Compare results with those from brute force */
      
      brute_force_matmul(a, b, bfRes);
      int flag=1;
      printf("%s","-------------------THIS IS THE RESULT OF BRUTE FORCE:-----------------------\n");
      for (int i=0;i<MAT_SIZE;i++){
         for(int j=0;j<MAT_SIZE;j++){
            //printf("%.1f ", bfRes[i][j]);
            if (bfRes[i][j]!=c[i*MAT_SIZE+j])//not match
            {
               flag=0;
               printf("wrong answer in %d,%d",i,j);
               /* code */
            }
            //if (j%50==0)
              // printf("%.1f %.1f \n", bfRes[i][j], c[i*MAT_SIZE+j]);
            
         }
         //printf("\n");
      }
     if (flag){
         printf("%s","The result is correct!\n");
      }
      else
      {
         printf("%s","The result is wrong!\n");
      }
      //printf("%d\n",flag);//1 means correct answer

      //free(a);
      //free(b);
      //free(local_res);

   }
   else
   {
      /* worker */
      /* Receive data from master and compute, then send back to master */
      MPI_Scatter(a, local_size*MAT_SIZE, MPI_DOUBLE, local_matrix, local_size*MAT_SIZE, MPI_DOUBLE, 0, comm);
      MPI_Bcast(b, MAT_SIZE*MAT_SIZE, MPI_DOUBLE, 0, comm);
      for(int i=0;i<local_size;i++)
         for(int j=0;j<MAT_SIZE;j++){
            double tmp=0;
            for(int k=0;k<MAT_SIZE;k++)
               tmp +=local_matrix[i*MAT_SIZE+k]*b[k*MAT_SIZE+j];
            local_res[i*MAT_SIZE+j]=tmp;
         }
      //free(local_matrix);
      //free(b);   
      MPI_Gather(local_res,local_size*MAT_SIZE,MPI_DOUBLE,c,local_size*MAT_SIZE,MPI_DOUBLE,0,comm);
   }

   /* Don't forget to finalize your MPI application */
   MPI_Finalize();
   return 0;
}
