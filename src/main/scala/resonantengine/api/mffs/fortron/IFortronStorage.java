package resonantengine.api.mffs.fortron;

public interface IFortronStorage {
   int getFortronEnergy();

   void setFortronEnergy(int var1);

   int getFortronCapacity();

   int requestFortron(int var1, boolean var2);

   int provideFortron(int var1, boolean var2);
}
