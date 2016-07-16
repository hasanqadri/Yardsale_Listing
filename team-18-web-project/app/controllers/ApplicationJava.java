
package controllers;

import models.LineItem;
import models.Transaction;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

public class ApplicationJava extends Controller {

    private final MailerClient mailer;

    @Inject
    public ApplicationJava(MailerClient mailer) {
        this.mailer = mailer;
    }

    public Result send(int saleId, int tranId) {
        List<LineItem> list = LineItem.findByTransactionId(tranId);
        Transaction t = Transaction.findById(tranId);
        float total = 0;
        for (LineItem li : list) {
            total = total + li.getUnitPrice();
        }
        final Email email = new Email()
                .setSubject("Yard sale receipt")
                .setFrom("team18email@gmail.com")
                .addTo(t.buyerEmail)
                //.addAttachment("favicon.png", new File(Play.application().classloader().getResource("public/images/favicon.png").getPath(), cid))
                //.addAttachment("data.txt", "data".getBytes(), "text/plain", "Simple data", EmailAttachment.INLINE)
                .setBodyText("A text message")
                .setBodyHtml("<html><body><div class=\"container content\">\n" +
                        "\n" +
                        "    <div class=\"row\">\n" +
                        "        <div class=\"col-sm-2\">\n" +
                        "            <h3 class=\"text-center\">Date</h3>\n" +
                        "            <h4 class=\"text-center\">" + t.formatDate() + "</h4>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-3\" >\n" +
                        "            <h3 class=\"text-center\">Buyer</h3>\n" +
                        "            <h4 class=\"text-center\">" + t.buyerName + "</h4>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-2\">\n" +
                        "            <h3 class=\"text-center\">Address</h3>\n" +
                        "            <h4 class=\"text-center\">"+ t.buyerAddress + "</h4>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-2\">\n" +
                        "            <h3 class=\"text-center\">Buyer Email</h3>\n" +
                        "            <h4  class=\"text-center\" >" + t.buyerEmail + "</h4>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-3\">\n" +
                        "            <h3 class=\"text-center\">Payment Method</h3>\n" +
                        "            <h4  class=\"text-center\" >" + t.paymentMethod + "</h4>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-3\">\n" +
                        "            <h3 class=\"text-center\">Total Price</h3>\n" +
                        "            <h4  class=\"text-center\" >" + total + "</h4>\n" +
                        "        </div>\n" +
                        "    </div>\n" + "</html>");
        String id = mailer.send(email);
        return redirect("/sale/" + saleId);
    }
}