
package controllers;

import models.LineItem;
import models.Sale;
import models.Transaction;
import models.User;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

public class EmailController extends Controller {

    private final MailerClient mailer;

    @Inject
    public EmailController(MailerClient mailer) {
        this.mailer = mailer;
    }

    public Result send(int saleId, int tranId) {
        List<LineItem> list = LineItem.findByTransactionId(tranId);
        Transaction t = Transaction.findById(tranId);
        double total = 0;
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

    public Result sendDonor(int saleId) {
        User u = User.findByUsername(session("username"));
        Sale s = Sale.findById(saleId);
        int tranId = 0;
        float total = 0;


        List<Transaction> transactions =
                Transaction.findCompletedBySaleId(saleId);
        if (!transactions.isEmpty()) {
            for (Transaction tr : transactions) {
                tranId = tr.id;
                List<LineItem> list = LineItem.findByTransactionId(tranId);
                if (!list.isEmpty()) {
                    for (LineItem l : list) {
                        total = total + l.getUnitPrice();
                    }
                }
            }
        }
        Transaction t = Transaction.findById(tranId);



        final Email email = new Email()
                .setSubject("Yard sale Donation Acknowledgement")
                .setFrom("team18email@gmail.com")
                .addTo(u.email)
                .setBodyText("Donor Acknowledgement Letter")
                .setBodyHtml("<html><link rel=\"stylesheet\" href=\"http://www.w3schools.com/lib/w3.css\">\n<body><div class=\"container content\">\n" +
                        "\n" +
                        "<div class=\"row\"><div class=\"col-sm-2\">\n" +
                        "\n " + u.firstName + " " + u.lastName + " \n<br>" + s.street + " \n<br>" +
                        s.city + ", " + s.state + ",<br> " + s.zip + "\n" +
                        "\n<br><p>" +
                        "Dear " + u.firstName + " " + u.lastName + " ," + "\n" +
                        "\n<br  ><br>" +
                        "Thank you so much for your very generous donation of " +  "$" + total + " to " + t.buyerName + " on " +  t.date + "\n" +
                        "\n<br><br>" +
                        "Respectfully,\n" +
                        "\n<br>" +
                        " Yardsale Inc.\n" + "<br>" +
                        " Heaven's Highway,\n" + "<br>" +
                        " GA, 30281\n" + "<br>" +
                        " team18email@gmail.com\n" + "<br><p>" +
                        "\n<p>" +
                        "<font align = 'center'> DONATION RECEIPT – Keep for your records\n </font>" + "<br>" +
                        "\n" +
                        "Organization:  \t" + "Yardsale Inc." + "\n" + "<br>" +
                        "Cash Contribution:  \t" + total + "<br>" +
                        "\n" +
                        "No goods or services were provided in exchange for your contribution.\n" + "<br>" +
                        "\n" +
                        "If you provided goods or services in exchange for the contribution, see IRS publication 1771 to determine if you are eligible.\n" +
                        "\n<br>" +
                        " \n<br>" +
                        "Note: The amount of the contribution that is deductible for federal income tax purposes is limited to the excess of money (and the fair market value of property other than money) contributed over the value of goods or services provided.\n</h3>\n" +
                        "    </div>\n" + "</html>");
        String id = mailer.send(email);
        return redirect("/sale/" + saleId);
    }
}