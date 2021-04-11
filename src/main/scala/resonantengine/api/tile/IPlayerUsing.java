package resonantengine.api.tile;

import scala.reflect.ScalaSignature;

import java.util.Set;

@ScalaSignature(
   bytes = "\u0006\u0001\u001d2q!\u0001\u0002\u0011\u0002G\u0005\u0011B\u0001\u0007J!2\f\u00170\u001a:Vg&twM\u0003\u0002\u0004\t\u0005!A/\u001b7f\u0015\t)a!A\u0002ba&T\u0011aB\u0001\u000fe\u0016\u001cxN\\1oi\u0016tw-\u001b8f\u0007\u0001\u0019\"\u0001\u0001\u0006\u0011\u0005-qQ\"\u0001\u0007\u000b\u00035\tQa]2bY\u0006L!a\u0004\u0007\u0003\r\u0005s\u0017PU3g\u0011\u0015\t\u0002A\"\u0001\u0013\u0003=9W\r\u001e)mCf,'o]+tS:<W#A\n\u0011\u0007QI2$D\u0001\u0016\u0015\t1r#\u0001\u0003vi&d'\"\u0001\r\u0002\t)\fg/Y\u0005\u00035U\u00111aU3u!\taR%D\u0001\u001e\u0015\tqr$\u0001\u0004qY\u0006LXM\u001d\u0006\u0003A\u0005\na!\u001a8uSRL(B\u0001\u0012$\u0003%i\u0017N\\3de\u00064GOC\u0001%\u0003\rqW\r^\u0005\u0003Mu\u0011A\"\u00128uSRL\b\u000b\\1zKJ\u0004"
)
public interface IPlayerUsing {
   Set getPlayersUsing();
}
