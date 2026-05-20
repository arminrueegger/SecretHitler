package ch.zli.mm233.console;

import ch.zli.mm233.engine.GameEngine;
import ch.zli.mm233.engine.model.GameState;
import ch.zli.mm233.engine.model.Phase;
import ch.zli.mm233.engine.model.Policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class ConsoleApp {

    private final ConsoleUi ui;

    public ConsoleApp(ConsoleUi ui) {
        this.ui = ui;
    }

    public static void main(String[] args) {
        ConsoleUi ui = new ConsoleUi(new Scanner(System.in), System.out);
        new ConsoleApp(ui).run();
    }

    public void run() {
        printTitleScreen();
        List<String> names = readNames();
        GameState state = GameEngine.newGame(names);
        printSecretRoles(state);
        while (state.phase() != Phase.GAME_OVER) {
            state = runRound(state);
        }
        ui.blank();
        ui.println("GAME OVER - " + state.winner());
    }

    private void printTitleScreen() {
        ui.println("                                              /$$$$$$                                            /$$           /$$   /$$ /$$   /$$     /$$                    ");
        ui.println("                                             /$$__  $$                                          | $$          | $$  | $$|__/  | $$    | $$                    ");
        ui.println("                                             | $$  \\__/  /$$$$$$   /$$$$$$$  /$$$$$$   /$$$$$$  /$$$$$$        | $$  | $$ /$$ /$$$$$$  | $$  /$$$$$$   /$$$$$$ ");
        ui.println("                                             |  $$$$$$  /$$__  $$ /$$_____/ /$$__  $$ /$$__  $$|_  $$_/        | $$$$$$$$| $$|_  $$_/  | $$ /$$__  $$ /$$__  $$");
        ui.println("                                              \\____  $$| $$$$$$$$| $$      | $$  \\__/| $$$$$$$$  | $$          | $$__  $$| $$  | $$    | $$| $$$$$$$$| $$  \\__/");
        ui.println("                                              /$$  \\ $$| $$_____/| $$      | $$      | $$_____/  | $$ /$$      | $$  | $$| $$  | $$ /$$| $$| $$_____/| $$      ");
        ui.println("                                             |  $$$$$$/|  $$$$$$$|  $$$$$$$| $$      |  $$$$$$$  |  $$$$/      | $$  | $$| $$  |  $$$$/| $$|  $$$$$$$| $$      ");
        ui.println("                                              \\______/  \\_______/ \\_______/|__/       \\_______/   \\___/        |__/  |__/|__/   \\___/  |__/ \\_______/|__/      ");
    ui.println(" . .. .                                                                    ..' . .                                                                     . .                                    .                        ");
        ui.println(" ' .                                                                      . '' .                                                                     .' ' .                                                            ");
        ui.println("' .                 ()                                                  . . '.                                                                     . '. .                                   pQ                         ");
        ui.println("                    ('                                                  .                                                                              .                                    ht                         ");
        ui.println("                    /                                                   ...                                                                      .   .                                      *|                         ");
        ui.println("                    t.                                                                                                                             .                                        Mc                         ");
        ui.println("                    f.        ..                                                                                                                                                            dn                         ");
        ui.println("                    tt      .                                                                          .                                                                                    /u                         ");
        ui.println("                    tr    .                                                   %@                   . ' ..                         nc                                          ....          1d                         ");
        ui.println("       .     '    . fj < '                                                   wpi8                . .                             0{@                                        . ' .           kk           .             ");
        ui.println("   'a  ,`'. .     `  '\" .^@                                                B   *8            ... ' ..{0dW8w.                  .WfvQ@                                      .. . .    a8  ji ...I''''a`^`^f,,/          ");
        ui.println(" p                ...'..'.. a%`                                             B\\ \\x$          . '.t8<`wnO|\",Yc|&M@<              ;m.|pW                                    . ' ..    @);a0mwpdkhaooo*aoaoaaooooaoZ       ");
        ui.println("   8\\]r[}))\\_|t\\\\rYZtxJfvtu\\Mw\"OO|                                     ?U. d^           QL<1)?  .I'^^\"I^'c }z>@t           v; ]@                                  .  '      @.k:\"*\"}k'U.*}'#.qJ\"{}@O;r|M#'        ");
        ui.println("   z~ W M  h8&1.`&8*ll .  % |x&)_/h                                         @CJnq@,       ^LB>u..\";_cCh/f\\jpj-i)  \\ t@O         k;+cj@                                    .   ;h &/Bd;p  t{BhB}|B<mBW$uZ@ < I\"         ");
        ui.println("   u< @ @rB@'Up%@~zx&B \\  W U ;:8c 0               . '                     @ 'l!,)x    .`@]; Q.  1l]fCWol |@n,rU1>.c .uWI     .dMpW#%8@                             . .       B|+x{.1 #  J|Wp@w`@@m\" @B_% & x'         ");
        ui.println("   f! @ Bp.@ h&'k;J@ % &  & B% j }ZC!X           ''  .                    \"*im*M*@l  .{a^' ..   <Wh;:LqjXn &0I{#p  .> -\"0a.':\"|Q1'.   0,                          ..  .     Wdt!|  Q h B  OJ^ @ -a @'<u;B& % l0         ");
        ui.println("   k1+@;hc<&'WX|*:XZx@ @  B @B u nOW             . .                    -/8&dUnCZq@.  . . .  {*,\\LkC\\\\a,  i#|pr (f\\B|.  . .. '~8.     %                        . .. .        o: @ & % B  //. @  I{@\"!hX@&.&.:d         ");
        ui.println("   @]@;.@B&:]%@@c f@#<    B#0B.( ?}@        .  ..                       .@8kowh#0b%~_---)*C'wh>d'pu  [-.#%} ; {CQ:O;0o^b0tt\\/t%o#wZ88&B@,                    .   .           o].@ @ B @ ~t[B]%_xZwr%WWB&BBB%wX         ");
        ui.println(" B@.@Bh{f-_<<!l;,^'.@'@.'BoM@vQ{tt, B      . . I\"        r        ;.       B      .d \\O'1O @u_*iQX`liZ [ Ii ,   ,U ;ia)+#.j)1p!:1      .        }[        \" .     .'        . #CI@ @  Xr  } -:<t      .'' &^:'nIkB      ");
        ui.println("@$p%}% O           z@8  m .@C@'p'[\\L:8@  ' .' @@       a?i      `0@       @    iaw`0^@(+YU.>I<]', 0\\~a;murrX_.. i nl'iY'vmk/B~p`dd\"    [       *<~      Uiu. ' ..YW|      '8U&)u8CZ.t&   @ * @.. ..      %:\\ k.la.     ");
        ui.println("<,@jc@ M          &]`@  Y  X%@.  f@dm8@' ' '.YLC1      &tZ      W{B      cB<Bwz-O.m|#<   n    _   <io\\ a'||, .  -.'Yc\"CQ.   \"*~p1Bv_)B\"n       mwa      @.& ..   )xB      \"O@8oc'  \":W   o @c%%  '       Zrm@@OB`@     ");
        ui.println("@!vlII v          .'^\" ~0v/wcf@[]??@[]mj<!!!aL{(@::,\"\"@X[z;....B&#%v     @ ;Bx@`@,0k;Y\"vk`@\"mU,&`%I^#\"W\"I^\"v,>+:Z,WiX#;Z:{C:&;CQ;#IbIw 0~\"^^^^*%%W``'''I@B&O'....b*M.     Ym#\\     Bd@ \"<j.`  `0..        .xl!XBOf.    ");
        ui.println("'%@(]@\\attftfjfff<BYt%#lzYJCQOmwpdbboahaaMM#*@hkdwZCYwuxffffrjrqxuuuvcU&&][[1@`''```'''''rnnnvcXzYYYUJJJCCCLQCUUYUYCCCX.....   .. )   \"rrjjjrvvxxxxxxxn%uuunnvvv@vvvczzzz&XzYXXYYX#Uh&B@,?Mcw#Y@WMM#oakdl;puYLY^@8v   ");
        ui.println("::.8%}`   ~, { 'u I&@@)`r`)^ %'Oa!BnOp %LIm8fB<_d`mb'#]l@_IB`{@noB.B\"mZ^Bm}xQh@1)\\tjxvzYCQ.  W1}vI.wmrf[uY,cu\\.knik{/  bmwqqpppqqww@!!I>:#Q~B.*X.@Yu@;8#?*ht@k)W!m% @B h@<@:W@>&8^@   bYW'`orvU@+}WI\\@\"v@^##JO#o;~ 18' ");
        ui.println("<& Oq80[;,\"\"^``'.._bMk~l!`'dxrjtt/\\|1)1{[[]??-_+~<<<>ii!!lll;;;::,\"\"\"^^`'*::\"aj...M....J...~X....d...p'....'w'.'@.....-..'@....Yr .)W  +                                            .k^. '{q%@@m         x,*ah&i|%h    ");
        ui.println(" pb..l@&hbpmOLUXvkC`\"l@q  [p(!l...'<:x]'....................\"^...... ;&'.`;@\"I}w    @\"`'L'    Y^ l@     .% .X)     @-lUi    (@@BB}~~&      .   .                       . .......''a11\\f&w!<?oZMM##hm*WWWz-]Wqn@     ");
        ui.println(" ]f&'~[  u8<8L*#(B@@B&|./ lO  8 oW.8, '@ Yd /M .@ \"&\".@  @ '8] L  `OI@B\"@J    a  +!  Z0fc  _      # .       .' v       : n   .)M^  o   WM oB+ %  f-f%k L  \"B\"@v u+  %\"@@> d  @!@B| o  '.+'1B]iB. lW0@.    ?1:k(%iUx    ");
        ui.println(" - W .^WB'@cBf _r`aU < .[ ;@@%1U/\\)+[ \"]wf{_~I^!'z/1-<<  IaWWM#&#p&kdwZCzU    #  -)[   /L  1      M .  '`,;I!. 0!~}tcQM\" z*:+]tX\"  o   M~Y:;;{'?1OBcccZn*oOQCQ0Z*p*&omwww*+|QYQJLOklCY' I. )  ^B;  !i\"#O   # n B&J     ");
        ui.println(" [ W ..BJ\\B Bu ?t``\\ > '~ :O C~}  '#\\ ;;c'  m| i'Z{  cf  x#t  cuu.8df  +kc    &  ]t' 'lhY  j      o         '  w       ` QUc?_kJ:  o   M<u' .o \" kk   w{\\ zO.  }Cc ~B   ZC\\ !c{  jw^` ' :. |  \"o!  :!  @^  * J % .     ");
        ui.println(" 1 M . *a,M BU {\\,;| ! \"l ,O Y }Il+u| ; j){1q\\ l.<0XJ8\"  f'n}to.v.W \\)td_u    %  [M{_{1>U  L<+-]})a  uvczYJC0  %dddkkkk. Jkmm:<al  a   M.{\\jv} ^ pXuuu[ ( YLunv\\ U <u_?]\\ | I^n(jY `^ ' ,. t   a_  ^l  *-  a O % '.    ");
        ui.println(" ( M ' # ^M'@Q'r)<Z? ! I\" \"O z [  'X) : t\"  kt ;'_{  %^  \\'j 'W Y M -  d[x    %  }BOC  pU  0      d         l  a         k}  d|m>  a  .o |. _| ` dZ   \\ { JJ;  u J >u   v \\ ;.~  Z.`\" ` \". f  .h1   i  *-  h w 8.^ .'  ");
        ui.println(" / # ' M d [p:%\"  .<.;.!' ^O u      1 ,      f ,`     ' .{   ...Y #'.'...j    B  1Ja1Zq{X  p^\"\"\"::m  1\\rzLmda  WtrxvYJQ  0:}1.;m+  k. 'a  .    . p      [ C      C i      \\ ,      `\" ` `' x      Wor  k{  d p & ^     ");
        ui.println(" j M ' WfOJzx\\{-<wC;.;.+  `O r  8?  { \"  L;: j \"^  .q '..}  r_. J o  o'J t    *  )ct^q*tY  h..    C  .      }  M      .  O uk-)w]  b   a  \\ {    d +.n  - C  +j  Q !  Q{  \\ \". kt  ', ^ '' u  _Biii>~<vBt  w m o ,     ");
        ui.println(" n # ` W \"h![tcQZ#1,.: ]  'O t,*^Mko1 ^ 8mBw%u ^^%{h-/x' -:pW*#IC a L@&(&/    d  |*h.oiOY  a '. . c  '......t  #    .`,  m,;._1m1..d. .M \"@oO\"   p QM8b + Lw@@%d 0 l WJcw | `.Wp@: ', ^ .` v  {&&&&&&W&Bu  C.m'k.:     ");
        ui.println(" c # ^ W #B@*^>{xw., , (  .O t^,    [ ^a`;   r `z +  . '.- b   .C kB*    M    L  /:?c'uJX .k ..   r  .      c  *      '  w>  MJO| .p   Wk    dU  W'  'LC< O   '8_m l! '.Oj\\ '@   f(.: \" .` X  t%|   taBBJ  J.0 k.l     ");
        ui.println(" Y # \" M {,UW   .Z'` \" r  .O ),I    ? `d;.   x ^\\. '  ' >`(    J d8Y    )    n  t{`a c'u' p      |  '      O  a      '  a#t!JCCt  q   WY    f.  q  .']_+! L   .1;m.:    q)\\ .'    u.; ,  ^ U  J?    j;^_Z  c.U q i     ");
        ui.println(" C # , # +!OB. . m'. ^ U   O {\">;||||c `d://ttm '/`-?+--' !'tlI;:J q&c    )    \\  f,\".'-^v  d      }  '      k  h      '  h&+rZhuu  w   MO:,^`r`  b~+-?/<I C-!\"'{iO':_-]]k)\\  ]?[1)C l :  ^ C  #<    n~^lp  j v 0.~     ");
        ui.println("  Q # : o.I<d&.   q^  `.m   O ]^-    _ `d,'   u.'x`... .' l'?    J Z&r    {    [  r\"^-O}lv. q   +  -  ^  }   #  d  l   ^  w!`|'wjX  m   Mm    x:  m    }i: Y    {-q ,    w)\\  ^    w ! l  \" 0  M;    nf.^#  / j 0.-     ");
        ui.println("a'Y  .*k'U&%$    %cra*@$@BBB&@l1    [8BW,' . &oBp,.   B&88.+    @@@&)    q'  W.`IBtrnXCh:|~B^  l 0~l;@  ?   W,'Yf <   O'.M|[1(\\xMiI8.  [k    x\\@Wo    {lrBB  ..1\\&8%+   w)[8B{    hl@%-_f0%o~ @\\0  .r@&M8 >%O|@O.[     ");
        ui.println(";@1fYW^' `]{]   .&     8   .#'Brrjc!,;ib1fcC@::Zadddb@^.'Bmwpdbwn?)#uXCZo^ .}&l\"Xq . . B    1  , j   p  +  'l   B ?  ;{ '.&.   \"////8d. Yd0JXuo^-}8CZp*8.\"+B|tcJwzY+Mxf\\)}&;:#+_]{|@hB^\":l#J   @,::I<<_]{(/@   @;a@    ");
        ui.println("@%&W%W%&B%88WM#okdpwda*a##mZon:.'.......'..''````''``\"`^`,`\"^^^\"\"\"\">,\",,::\"  OBB88M&/xucY@   'ktnvcq   8nxrjtc0   @tXncm0 .^@(fnzZdqM88%]-]--~+<+>><fi!!!~II;I::;::,:,,::::,:::,:::::~\"     .hmm0OmwZZqddbbdkbkkkkmWUmj  ");
        ui.println("+..' c.'vkB*M@k  c@1 B18.`C^t:q?kzp:I;ICQCd<`;/)x,l`\"{,]_\\X_,fancYC%vhmJUpJY/..aW{}[]]?---??[}}{1(\\tfrxnnuvvvcunxrrft/\\|()1{1{11M!?  @hdk*,Li'?QZpkwc}Wq|zQm0kkxQ<+--b>W]<;_Jxa:J#_\\til-nu #'t\"\"lix#@oxW@  f `', J:@   ");
        ui.println("/1~?])X|/'0   :#rrr@/zBc@JYYQqqcJ <mv|/kj\" .*t1?f;BI?@XbOCn)+;+.      a#aqOz)B .B~_-]}{1(|/tfjxnczXYUJCCUUXcuunxrxxxnuczzXXXXzzcccB/  %n@M##*J;\",i[tzLkW%%**qkj[fQ:  C_  ;z\"}+L/j  @nmU\\v*p\\B[XXvX0%^    @)pdLmdmt#w@   ");
        ui.println("ft...v    h    &` .@,,B.@ \"-MaakOt-I^     'l1jj)x!zvxfrrrjxxrJxjjfY///wUOpZ <J[}}{{{1(|/frxuvczYUJCQCCCJCUUXYcvunxjt/\\|/|\\|\\|\\\\\\\\\\tfrrxx Xwa#*k/rxZzJUUQJZ0O000m0QQ000:i//xf{!\",l?fQa%8Bk0t<'@  l  ,U.    @ {',>}nnX @   ");
        ui.println("QpW#pwCj?;\",:\\_l+(|)-_\"'.;\":;{l->~-?1}O)|\\Jttttt/U\\|)1t[]?-L+~~>ii!lI<;Ll[jw`*[?-_+++++~~<>><<><<<~~++_-?[}1)(||/fjrrxxxxxrrft/\\|(1{[[[]W:ii/<Z^x,,,0;!;I}!!i>~*_-]|}1|\\0fxxuOcXYwCLZQ00QZQQCCJ   .`\"l-|rf|}{rCwh**MU@-!\"");
        ui.println("}_<l;`'`l`....+'''n'^'^```b\",:\\;;;I!li!>>><~++___-}??)][[[r{}/1t1)))((w}v0B@]]]][??]]]]?--]]-?]??]???]]?]]]][]]][[]]]][][]]][[]]]]]][]]][]Ml.'tb\",,:o!!>f-?]]][11t{0{f}([[[]L]??-\\_f++j~>1!~lI::\"\"^``''''.......           ");
        ui.println("                            .. .                                     JkoWW++__??[]}}1{(1|(\\\\t/jfxrnxnxnxnxnrnxnrnxnrrfj/t\\\\((11{{[[]]--[@WQd`                                                                 ..''`^\",\"");
        ui.println("                            .  '. .                                                                      ...                                                                                                            ");
        ui.println("                               .                                                                     . .. .                                                                      . .                                   ");
        ui.println("                        .                                                                                                                                                        .  ..                                  ");
        ui.println("                       ...                                                                      .   ..                                                                        .   .                                    ");
        ui.blank();
        ui.println("                                                                                                 SECRET HITLER");
        ui.blank();
        ui.println("                                                                                             Press ENTER to begin...");
        ui.promptLine("");
    }

    private void printSecretRoles(GameState state) {
        ui.blank();
        ui.println("=== SECRET ROLE ASSIGNMENTS ===");
        state.players().forEach(p ->
                ui.println("  " + p.name() + " -> " + p.role()));
        ui.blank();
        ui.println("Press ENTER to continue (clear screen first!)");
        ui.promptLine("");
    }

    private List<String> readNames() {
        int n = ui.promptInt("How many players?", 5, 10);
        List<String> names = new ArrayList<>(n);
        IntStream.range(0, n).forEach(i ->
                names.add(ui.promptLine("Name of player " + i)));
        return List.copyOf(names);
    }

    private GameState runRound(GameState s) {
        printPublicState(s);
        int chancellorIdx = nominateChancellor(s);
        boolean elected = collectVotes(s);
        if (!elected) {
            ui.println("Vote FAILED. Next president.");
            return GameEngine.failedElection(s);
        }
        ui.println("Vote PASSED. Chancellor is " + s.players().get(chancellorIdx).name() + ".");
        return runLegislativeSession(s, chancellorIdx);
    }

    private void printPublicState(GameState s) {
        ui.blank();
        ui.println("--- Round ---");
        ui.println("Liberal: " + s.liberalPolicies() + "/5    Fascist: " + s.fascistPolicies() + "/6");
        ui.println("President: " + s.players().get(s.presidentIndex()).name()
                + " (#" + s.presidentIndex() + ")");
    }

    private int nominateChancellor(GameState s) {
        ui.println("Players:");
        s.players().forEach(p -> ui.println("  " + p.id() + " = " + p.name()));
        int idx;
        do {
            idx = ui.promptInt("President picks chancellor", 0, s.players().size() - 1);
            if (idx == s.presidentIndex()) {
                ui.println("  cannot pick yourself, try again");
            }
        } while (idx == s.presidentIndex());
        return idx;
    }

    private boolean collectVotes(GameState s) {
        long yes = s.players().stream()
                .map(p -> ui.promptYesNo(p.name() + " votes"))
                .filter(v -> v)
                .count();
        long no = s.players().size() - yes;
        ui.println("Result: " + yes + " Ja / " + no + " Nein");
        return yes > no;
    }

    private GameState runLegislativeSession(GameState s, int chancellorIdx) {
        GameEngine.DrawResult dr = GameEngine.drawThree(s);
        List<Policy> drawn = dr.drawn();
        String prezName = s.players().get(s.presidentIndex()).name();
        String chancName = s.players().get(chancellorIdx).name();

        ui.println(prezName + " (President) drew: " + drawn);
        int presDiscardIdx = ui.promptInt(prezName + ", which card to DISCARD?", 0, 2);
        Policy presDiscard = drawn.get(presDiscardIdx);
        List<Policy> twoCards = removeIndex(drawn, presDiscardIdx);

        ui.println(chancName + " (Chancellor) receives: " + twoCards);
        int chancDiscardIdx = ui.promptInt(chancName + ", which card to DISCARD?", 0, 1);
        Policy chancDiscard = twoCards.get(chancDiscardIdx);
        Policy enacted = twoCards.get(1 - chancDiscardIdx);

        ui.println("Policy enacted: " + enacted);
        return GameEngine.enactPolicy(dr.state(), enacted, presDiscard, chancDiscard);
    }

    private static <T> List<T> removeIndex(List<T> list, int idx) {
        return IntStream.range(0, list.size())
                .filter(i -> i != idx)
                .mapToObj(list::get)
                .toList();
    }
}
