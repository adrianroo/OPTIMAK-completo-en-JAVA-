package co.optimak.modelo;

public class PausaModulo {

    private long idPausa;
    private String tipoPausa;
    private String horaInicioPausa;
    private int duracionMinutos;
    private long idJornadaModulo;

    public PausaModulo() {}

    public PausaModulo(long idPausa, String tipoPausa, String horaInicioPausa,
                       int duracionMinutos, long idJornadaModulo) {
        this.idPausa = idPausa;
        this.tipoPausa = tipoPausa;
        this.horaInicioPausa = horaInicioPausa;
        this.duracionMinutos = duracionMinutos;
        this.idJornadaModulo = idJornadaModulo;
    }

    public long getIdPausa() { return idPausa; }
    public void setIdPausa(long idPausa) { this.idPausa = idPausa; }

    public String getTipoPausa() { return tipoPausa; }
    public void setTipoPausa(String tipoPausa) { this.tipoPausa = tipoPausa; }

    public String getHoraInicioPausa() { return horaInicioPausa; }
    public void setHoraInicioPausa(String horaInicioPausa) { this.horaInicioPausa = horaInicioPausa; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public long getIdJornadaModulo() { return idJornadaModulo; }
    public void setIdJornadaModulo(long idJornadaModulo) { this.idJornadaModulo = idJornadaModulo; }
}
