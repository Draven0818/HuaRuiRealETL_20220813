/**
 * @ClassName Test02
 * @Description TODO
 * @Author jiangweiye
 * @Date 2022/2/22 8:22 下午
 * @Version 1.0
 */
public class Test02 {
    public static void main(String[] args) {
        TT tt = new T1();
    }
}


class TT {
    public void p1() {
    }

    public void p2() {
    }
}

class T1 extends TT {
    public void pT1() {

    }

    @Override
    public void p1() {
        super.p1();
    }

    @Override
    public void p2() {
        super.p2();
    }
}

class T2 extends TT {
    public void pT2() {

    }
}