import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.ADB_RealmTermFactory;

public class ArcadeImplTest01 {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmFunctionNotSupportedException {

        CoreRealm defaultCoreRealm = ADB_RealmTermFactory.getDefaultCoreRealm();
        //

       // System.out.println(defaultCoreRealm.getCoreRealmName());

        //CoreRealm targetCoreRealm = RealmTermFactory.createCoreRealm("wycTest002");
        //System.out.println(targetCoreRealm);

       // System.out.println(RealmTermFactory.listCoreRealms());

        ConceptionKind newConceptionKind = defaultCoreRealm.createConceptionKind("ConceptionKind17","ConceptionKind17描述");
        System.out.println(newConceptionKind.getConceptionKindName());
        System.out.println(newConceptionKind.getConceptionKindDesc());
    }
}
