package org.sunflow.image;

/**
 * This class is an abstract interface to sampled or analytic spectral data.
 */
public abstract class SpectralCurve {
    /**
     * This function determines the actual spectral curve data. Note that the
     * lambda parameter is assumed to be in nanometers.
     * 
     * @param lambda wavelength to sample in nanometers
     * @return the value of the spectral curve at this point
     */
    public abstract float sample(float lambda);

    /**
     * The following data was taken from:
     * http://www.cs.utah.edu/classes/cs7650/macbeth/
     */
    private static final int WAVELENGTH_MIN = 375;
    private static final int WAVELENGTH_MAX = 800;
    // TODO: decide if this data is the "best" to use
    private static final float CIE_COLOR_MATCHING_CURVES[][] = {
            { 0.00003f, 0.00000f, 0.00013f }, { 0.00023f, 0.00000f, 0.00107f },
            { 0.00056f, 0.00000f, 0.00259f }, { 0.00094f, 0.00000f, 0.00432f },
            { 0.00127f, 0.00000f, 0.00589f }, { 0.00150f, 0.00001f, 0.00698f },
            { 0.00165f, 0.00003f, 0.00771f }, { 0.00176f, 0.00005f, 0.00831f },
            { 0.00189f, 0.00007f, 0.00897f }, { 0.00207f, 0.00009f, 0.00988f },
            { 0.00235f, 0.00010f, 0.01120f }, { 0.00267f, 0.00010f, 0.01276f },
            { 0.00304f, 0.00010f, 0.01454f }, { 0.00346f, 0.00010f, 0.01657f },
            { 0.00394f, 0.00010f, 0.01885f }, { 0.00447f, 0.00011f, 0.02138f },
            { 0.00501f, 0.00012f, 0.02396f }, { 0.00561f, 0.00014f, 0.02677f },
            { 0.00629f, 0.00016f, 0.03002f }, { 0.00712f, 0.00019f, 0.03393f },
            { 0.00813f, 0.00022f, 0.03869f }, { 0.00930f, 0.00025f, 0.04423f },
            { 0.01061f, 0.00029f, 0.05041f }, { 0.01202f, 0.00034f, 0.05712f },
            { 0.01353f, 0.00038f, 0.06423f }, { 0.01505f, 0.00042f, 0.07145f },
            { 0.01645f, 0.00045f, 0.07808f }, { 0.01790f, 0.00048f, 0.08496f },
            { 0.01962f, 0.00051f, 0.09314f }, { 0.02184f, 0.00056f, 0.10370f },
            { 0.02471f, 0.00064f, 0.11740f }, { 0.02802f, 0.00074f, 0.13322f },
            { 0.03177f, 0.00085f, 0.15120f }, { 0.03604f, 0.00098f, 0.17161f },
            { 0.04086f, 0.00112f, 0.19473f }, { 0.04625f, 0.00128f, 0.22059f },
            { 0.05200f, 0.00145f, 0.24819f }, { 0.05827f, 0.00162f, 0.27828f },
            { 0.06527f, 0.00183f, 0.31191f }, { 0.07321f, 0.00207f, 0.35014f },
            { 0.08225f, 0.00234f, 0.39372f }, { 0.09223f, 0.00263f, 0.44172f },
            { 0.10311f, 0.00296f, 0.49410f }, { 0.11491f, 0.00332f, 0.55107f },
            { 0.12766f, 0.00376f, 0.61285f }, { 0.14154f, 0.00426f, 0.68033f },
            { 0.15714f, 0.00485f, 0.75637f }, { 0.17383f, 0.00549f, 0.83784f },
            { 0.19077f, 0.00619f, 0.92075f }, { 0.20711f, 0.00692f, 1.00112f },
            { 0.22228f, 0.00769f, 1.07614f }, { 0.23713f, 0.00849f, 1.14994f },
            { 0.25159f, 0.00933f, 1.22211f }, { 0.26533f, 0.01021f, 1.29115f },
            { 0.27803f, 0.01113f, 1.35558f }, { 0.28946f, 0.01208f, 1.41427f },
            { 0.29986f, 0.01306f, 1.46841f }, { 0.30928f, 0.01408f, 1.51820f },
            { 0.31771f, 0.01514f, 1.56354f }, { 0.32515f, 0.01624f, 1.60434f },
            { 0.33157f, 0.01738f, 1.64040f }, { 0.33689f, 0.01857f, 1.67135f },
            { 0.34120f, 0.01980f, 1.69767f }, { 0.34461f, 0.02106f, 1.71995f },
            { 0.34724f, 0.02235f, 1.73879f }, { 0.34914f, 0.02365f, 1.75445f },
            { 0.35008f, 0.02495f, 1.76571f }, { 0.35018f, 0.02628f, 1.77326f },
            { 0.34965f, 0.02764f, 1.77812f }, { 0.34868f, 0.02906f, 1.78131f },
            { 0.34741f, 0.03056f, 1.78349f }, { 0.34557f, 0.03210f, 1.78338f },
            { 0.34323f, 0.03370f, 1.78132f }, { 0.34055f, 0.03536f, 1.77799f },
            { 0.33767f, 0.03710f, 1.77407f }, { 0.33472f, 0.03892f, 1.77022f },
            { 0.33169f, 0.04080f, 1.76646f }, { 0.32845f, 0.04276f, 1.76213f },
            { 0.32489f, 0.04480f, 1.75650f }, { 0.32090f, 0.04691f, 1.74888f },
            { 0.31638f, 0.04911f, 1.73881f }, { 0.31147f, 0.05139f, 1.72716f },
            { 0.30614f, 0.05376f, 1.71372f }, { 0.30036f, 0.05620f, 1.69801f },
            { 0.29411f, 0.05871f, 1.67958f }, { 0.28738f, 0.06130f, 1.65822f },
            { 0.28026f, 0.06393f, 1.63487f }, { 0.27269f, 0.06663f, 1.60900f },
            { 0.26456f, 0.06943f, 1.57982f }, { 0.25577f, 0.07237f, 1.54653f },
            { 0.24617f, 0.07546f, 1.50810f }, { 0.23549f, 0.07865f, 1.46342f },
            { 0.22410f, 0.08197f, 1.41439f }, { 0.21244f, 0.08544f, 1.36326f },
            { 0.20094f, 0.08909f, 1.31231f }, { 0.18994f, 0.09296f, 1.26308f },
            { 0.17902f, 0.09700f, 1.21318f }, { 0.16818f, 0.10122f, 1.16290f },
            { 0.15751f, 0.10563f, 1.11322f }, { 0.14714f, 0.11023f, 1.06508f },
            { 0.13713f, 0.11503f, 1.01942f }, { 0.12737f, 0.12004f, 0.97620f },
            { 0.11787f, 0.12526f, 0.93452f }, { 0.10868f, 0.13064f, 0.89346f },
            { 0.09986f, 0.13618f, 0.85209f }, { 0.09143f, 0.14183f, 0.80939f },
            { 0.08330f, 0.14754f, 0.76484f }, { 0.07552f, 0.15338f, 0.71986f },
            { 0.06815f, 0.15947f, 0.67607f }, { 0.06125f, 0.16591f, 0.63511f },
            { 0.05489f, 0.17278f, 0.59834f }, { 0.04907f, 0.17992f, 0.56498f },
            { 0.04373f, 0.18738f, 0.53427f }, { 0.03880f, 0.19525f, 0.50562f },
            { 0.03420f, 0.20361f, 0.47843f }, { 0.02988f, 0.21252f, 0.45229f },
            { 0.02593f, 0.22187f, 0.42779f }, { 0.02234f, 0.23169f, 0.40489f },
            { 0.01906f, 0.24203f, 0.38339f }, { 0.01609f, 0.25293f, 0.36307f },
            { 0.01338f, 0.26439f, 0.34383f }, { 0.01098f, 0.27628f, 0.32598f },
            { 0.00888f, 0.28869f, 0.30939f }, { 0.00707f, 0.30178f, 0.29384f },
            { 0.00555f, 0.31569f, 0.27912f }, { 0.00431f, 0.33057f, 0.26513f },
            { 0.00332f, 0.34647f, 0.25233f }, { 0.00261f, 0.36319f, 0.24045f },
            { 0.00223f, 0.38055f, 0.22912f }, { 0.00224f, 0.39832f, 0.21795f },
            { 0.00265f, 0.41637f, 0.20663f }, { 0.00337f, 0.43487f, 0.19542f },
            { 0.00446f, 0.45384f, 0.18442f }, { 0.00599f, 0.47323f, 0.17370f },
            { 0.00805f, 0.49299f, 0.16328f }, { 0.01069f, 0.51315f, 0.15318f },
            { 0.01384f, 0.53397f, 0.14328f }, { 0.01749f, 0.55522f, 0.13366f },
            { 0.02170f, 0.57659f, 0.12446f }, { 0.02648f, 0.59777f, 0.11579f },
            { 0.03188f, 0.61860f, 0.10777f }, { 0.03792f, 0.63958f, 0.10030f },
            { 0.04454f, 0.66047f, 0.09335f }, { 0.05170f, 0.68093f, 0.08691f },
            { 0.05933f, 0.70059f, 0.08098f }, { 0.06740f, 0.71914f, 0.07557f },
            { 0.07600f, 0.73678f, 0.07082f }, { 0.08509f, 0.75366f, 0.06660f },
            { 0.09462f, 0.76988f, 0.06275f }, { 0.10453f, 0.78555f, 0.05910f },
            { 0.11478f, 0.80073f, 0.05554f }, { 0.12553f, 0.81540f, 0.05226f },
            { 0.13668f, 0.82949f, 0.04922f }, { 0.14811f, 0.84298f, 0.04635f },
            { 0.15969f, 0.85583f, 0.04357f }, { 0.17133f, 0.86800f, 0.04084f },
            { 0.18310f, 0.87947f, 0.03819f }, { 0.19505f, 0.89031f, 0.03565f },
            { 0.20717f, 0.90055f, 0.03322f }, { 0.21948f, 0.91024f, 0.03091f },
            { 0.23197f, 0.91943f, 0.02872f }, { 0.24463f, 0.92805f, 0.02665f },
            { 0.25748f, 0.93611f, 0.02471f }, { 0.27051f, 0.94365f, 0.02287f },
            { 0.28372f, 0.95067f, 0.02113f }, { 0.29712f, 0.95720f, 0.01949f },
            { 0.31071f, 0.96320f, 0.01797f }, { 0.32449f, 0.96867f, 0.01655f },
            { 0.33844f, 0.97367f, 0.01523f }, { 0.35257f, 0.97820f, 0.01399f },
            { 0.36687f, 0.98228f, 0.01283f }, { 0.38133f, 0.98587f, 0.01177f },
            { 0.39596f, 0.98898f, 0.01081f }, { 0.41078f, 0.99167f, 0.00992f },
            { 0.42581f, 0.99398f, 0.00909f }, { 0.44105f, 0.99594f, 0.00833f },
            { 0.45653f, 0.99753f, 0.00764f }, { 0.47221f, 0.99873f, 0.00702f },
            { 0.48806f, 0.99954f, 0.00646f }, { 0.50406f, 0.99995f, 0.00594f },
            { 0.52018f, 0.99996f, 0.00547f }, { 0.53650f, 0.99959f, 0.00505f },
            { 0.55298f, 0.99884f, 0.00469f }, { 0.56955f, 0.99765f, 0.00436f },
            { 0.58618f, 0.99601f, 0.00405f }, { 0.60283f, 0.99387f, 0.00375f },
            { 0.61957f, 0.99124f, 0.00348f }, { 0.63637f, 0.98815f, 0.00323f },
            { 0.65320f, 0.98463f, 0.00300f }, { 0.67001f, 0.98070f, 0.00279f },
            { 0.68679f, 0.97640f, 0.00261f }, { 0.70363f, 0.97168f, 0.00247f },
            { 0.72047f, 0.96656f, 0.00234f }, { 0.73724f, 0.96104f, 0.00224f },
            { 0.75386f, 0.95511f, 0.00215f }, { 0.77030f, 0.94878f, 0.00206f },
            { 0.78667f, 0.94203f, 0.00198f }, { 0.80292f, 0.93488f, 0.00192f },
            { 0.81897f, 0.92734f, 0.00187f }, { 0.83474f, 0.91946f, 0.00182f },
            { 0.85020f, 0.91125f, 0.00178f }, { 0.86549f, 0.90269f, 0.00176f },
            { 0.88053f, 0.89377f, 0.00175f }, { 0.89521f, 0.88451f, 0.00174f },
            { 0.90942f, 0.87492f, 0.00172f }, { 0.92306f, 0.86499f, 0.00168f },
            { 0.93630f, 0.85468f, 0.00162f }, { 0.94908f, 0.84402f, 0.00156f },
            { 0.96135f, 0.83308f, 0.00150f }, { 0.97301f, 0.82193f, 0.00143f },
            { 0.98404f, 0.81061f, 0.00137f }, { 0.99453f, 0.79901f, 0.00130f },
            { 1.00444f, 0.78717f, 0.00124f }, { 1.01372f, 0.77517f, 0.00118f },
            { 1.02230f, 0.76307f, 0.00112f }, { 1.03015f, 0.75091f, 0.00108f },
            { 1.03748f, 0.73863f, 0.00106f }, { 1.04414f, 0.72624f, 0.00104f },
            { 1.04996f, 0.71375f, 0.00103f }, { 1.05475f, 0.70120f, 0.00101f },
            { 1.05836f, 0.68858f, 0.00098f }, { 1.06088f, 0.67587f, 0.00095f },
            { 1.06239f, 0.66308f, 0.00091f }, { 1.06296f, 0.65025f, 0.00086f },
            { 1.06266f, 0.63741f, 0.00082f }, { 1.06155f, 0.62459f, 0.00078f },
            { 1.05968f, 0.61175f, 0.00074f }, { 1.05693f, 0.59889f, 0.00071f },
            { 1.05322f, 0.58604f, 0.00067f }, { 1.04843f, 0.57321f, 0.00062f },
            { 1.04246f, 0.56039f, 0.00057f }, { 1.03531f, 0.54757f, 0.00051f },
            { 1.02708f, 0.53475f, 0.00044f }, { 1.01789f, 0.52198f, 0.00038f },
            { 1.00788f, 0.50930f, 0.00032f }, { 0.99713f, 0.49673f, 0.00028f },
            { 0.98552f, 0.48426f, 0.00025f }, { 0.97306f, 0.47188f, 0.00023f },
            { 0.95978f, 0.45956f, 0.00022f }, { 0.94572f, 0.44731f, 0.00021f },
            { 0.93089f, 0.43511f, 0.00020f }, { 0.91525f, 0.42302f, 0.00020f },
            { 0.89882f, 0.41099f, 0.00020f }, { 0.88162f, 0.39899f, 0.00020f },
            { 0.86366f, 0.38700f, 0.00020f }, { 0.84490f, 0.37497f, 0.00019f },
            { 0.82509f, 0.36286f, 0.00018f }, { 0.80446f, 0.35074f, 0.00016f },
            { 0.78334f, 0.33870f, 0.00013f }, { 0.76203f, 0.32684f, 0.00011f },
            { 0.74070f, 0.31520f, 0.00009f }, { 0.71887f, 0.30365f, 0.00007f },
            { 0.69674f, 0.29225f, 0.00005f }, { 0.67466f, 0.28109f, 0.00003f },
            { 0.65298f, 0.27026f, 0.00001f }, { 0.63198f, 0.25985f, 0.00000f },
            { 0.61142f, 0.24982f, 0.00000f }, { 0.59121f, 0.24012f, 0.00000f },
            { 0.57130f, 0.23071f, 0.00000f }, { 0.55165f, 0.22152f, 0.00000f },
            { 0.53221f, 0.21253f, 0.00000f }, { 0.51300f, 0.20380f, 0.00000f },
            { 0.49406f, 0.19530f, 0.00000f }, { 0.47539f, 0.18703f, 0.00000f },
            { 0.45699f, 0.17896f, 0.00000f }, { 0.43887f, 0.17108f, 0.00000f },
            { 0.42095f, 0.16340f, 0.00000f }, { 0.40331f, 0.15593f, 0.00000f },
            { 0.38599f, 0.14866f, 0.00000f }, { 0.36908f, 0.14163f, 0.00000f },
            { 0.35262f, 0.13483f, 0.00000f }, { 0.33650f, 0.12825f, 0.00000f },
            { 0.32076f, 0.12189f, 0.00000f }, { 0.30546f, 0.11576f, 0.00000f },
            { 0.29068f, 0.10986f, 0.00000f }, { 0.27646f, 0.10420f, 0.00000f },
            { 0.26279f, 0.09880f, 0.00000f }, { 0.24963f, 0.09364f, 0.00000f },
            { 0.23694f, 0.08868f, 0.00000f }, { 0.22468f, 0.08392f, 0.00000f },
            { 0.21283f, 0.07933f, 0.00000f }, { 0.20143f, 0.07493f, 0.00000f },
            { 0.19048f, 0.07074f, 0.00000f }, { 0.17995f, 0.06672f, 0.00000f },
            { 0.16982f, 0.06287f, 0.00000f }, { 0.16008f, 0.05917f, 0.00000f },
            { 0.15074f, 0.05565f, 0.00000f }, { 0.14180f, 0.05230f, 0.00000f },
            { 0.13326f, 0.04911f, 0.00000f }, { 0.12512f, 0.04607f, 0.00000f },
            { 0.11737f, 0.04317f, 0.00000f }, { 0.11002f, 0.04042f, 0.00000f },
            { 0.10306f, 0.03782f, 0.00000f }, { 0.09649f, 0.03538f, 0.00000f },
            { 0.09033f, 0.03309f, 0.00000f }, { 0.08458f, 0.03095f, 0.00000f },
            { 0.07930f, 0.02900f, 0.00000f }, { 0.07444f, 0.02720f, 0.00000f },
            { 0.06991f, 0.02553f, 0.00000f }, { 0.06565f, 0.02396f, 0.00000f },
            { 0.06162f, 0.02247f, 0.00000f }, { 0.05795f, 0.02111f, 0.00000f },
            { 0.05458f, 0.01987f, 0.00000f }, { 0.05141f, 0.01870f, 0.00000f },
            { 0.04833f, 0.01756f, 0.00000f }, { 0.04528f, 0.01644f, 0.00000f },
            { 0.04230f, 0.01535f, 0.00000f }, { 0.03944f, 0.01429f, 0.00000f },
            { 0.03671f, 0.01329f, 0.00000f }, { 0.03413f, 0.01235f, 0.00000f },
            { 0.03172f, 0.01147f, 0.00000f }, { 0.02946f, 0.01065f, 0.00000f },
            { 0.02736f, 0.00989f, 0.00000f }, { 0.02540f, 0.00918f, 0.00000f },
            { 0.02357f, 0.00852f, 0.00000f }, { 0.02187f, 0.00790f, 0.00000f },
            { 0.02031f, 0.00733f, 0.00000f }, { 0.01889f, 0.00682f, 0.00000f },
            { 0.01758f, 0.00635f, 0.00000f }, { 0.01637f, 0.00591f, 0.00000f },
            { 0.01525f, 0.00550f, 0.00000f }, { 0.01426f, 0.00514f, 0.00000f },
            { 0.01337f, 0.00482f, 0.00000f }, { 0.01256f, 0.00452f, 0.00000f },
            { 0.01178f, 0.00424f, 0.00000f }, { 0.01102f, 0.00396f, 0.00000f },
            { 0.01030f, 0.00370f, 0.00000f }, { 0.00962f, 0.00345f, 0.00000f },
            { 0.00898f, 0.00322f, 0.00000f }, { 0.00838f, 0.00300f, 0.00000f },
            { 0.00783f, 0.00280f, 0.00000f }, { 0.00732f, 0.00262f, 0.00000f },
            { 0.00685f, 0.00246f, 0.00000f }, { 0.00641f, 0.00231f, 0.00000f },
            { 0.00600f, 0.00217f, 0.00000f }, { 0.00560f, 0.00203f, 0.00000f },
            { 0.00523f, 0.00190f, 0.00000f }, { 0.00488f, 0.00178f, 0.00000f },
            { 0.00455f, 0.00167f, 0.00000f }, { 0.00425f, 0.00156f, 0.00000f },
            { 0.00396f, 0.00145f, 0.00000f }, { 0.00369f, 0.00134f, 0.00000f },
            { 0.00345f, 0.00123f, 0.00000f }, { 0.00322f, 0.00113f, 0.00000f },
            { 0.00301f, 0.00104f, 0.00000f }, { 0.00280f, 0.00096f, 0.00000f },
            { 0.00260f, 0.00089f, 0.00000f }, { 0.00241f, 0.00083f, 0.00000f },
            { 0.00224f, 0.00078f, 0.00000f }, { 0.00208f, 0.00073f, 0.00000f },
            { 0.00193f, 0.00068f, 0.00000f }, { 0.00179f, 0.00063f, 0.00000f },
            { 0.00167f, 0.00059f, 0.00000f }, { 0.00156f, 0.00055f, 0.00000f },
            { 0.00145f, 0.00052f, 0.00000f }, { 0.00135f, 0.00049f, 0.00000f },
            { 0.00126f, 0.00047f, 0.00000f }, { 0.00118f, 0.00045f, 0.00000f },
            { 0.00111f, 0.00043f, 0.00000f }, { 0.00104f, 0.00041f, 0.00000f },
            { 0.00097f, 0.00038f, 0.00000f }, { 0.00090f, 0.00034f, 0.00000f },
            { 0.00084f, 0.00029f, 0.00000f }, { 0.00078f, 0.00025f, 0.00000f },
            { 0.00073f, 0.00021f, 0.00000f }, { 0.00068f, 0.00019f, 0.00000f },
            { 0.00063f, 0.00019f, 0.00000f }, { 0.00059f, 0.00019f, 0.00000f },
            { 0.00056f, 0.00020f, 0.00000f }, { 0.00052f, 0.00020f, 0.00000f },
            { 0.00048f, 0.00019f, 0.00000f }, { 0.00044f, 0.00017f, 0.00000f },
            { 0.00039f, 0.00015f, 0.00000f }, { 0.00035f, 0.00013f, 0.00000f },
            { 0.00032f, 0.00011f, 0.00000f }, { 0.00029f, 0.00010f, 0.00000f },
            { 0.00026f, 0.00009f, 0.00000f }, { 0.00024f, 0.00009f, 0.00000f },
            { 0.00022f, 0.00010f, 0.00000f }, { 0.00021f, 0.00010f, 0.00000f },
            { 0.00020f, 0.00010f, 0.00000f }, { 0.00020f, 0.00010f, 0.00000f },
            { 0.00020f, 0.00011f, 0.00000f }, { 0.00020f, 0.00011f, 0.00000f },
            { 0.00020f, 0.00010f, 0.00000f }, { 0.00019f, 0.00009f, 0.00000f },
            { 0.00017f, 0.00008f, 0.00000f }, { 0.00015f, 0.00005f, 0.00000f },
            { 0.00013f, 0.00003f, 0.00000f }, { 0.00011f, 0.00001f, 0.00000f },
            { 0.00010f, 0.00000f, 0.00000f }, { 0.00009f, 0.00000f, 0.00000f },
            { 0.00009f, 0.00000f, 0.00000f }, { 0.00010f, 0.00000f, 0.00000f },
            { 0.00010f, 0.00000f, 0.00000f }, { 0.00010f, 0.00000f, 0.00000f },
            { 0.00010f, 0.00000f, 0.00000f }, { 0.00011f, 0.00000f, 0.00000f },
            { 0.00011f, 0.00000f, 0.00000f }, { 0.00010f, 0.00000f, 0.00000f },
            { 0.00009f, 0.00000f, 0.00000f }, { 0.00008f, 0.00000f, 0.00000f },
            { 0.00005f, 0.00000f, 0.00000f }, { 0.00003f, 0.00000f, 0.00000f },
            { 0.00001f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f }, { 0.00000f, 0.00000f, 0.00000f },
            { 0.00000f, 0.00000f, 0.00000f } };
    private static final int WAVELENGTH_STEP = (WAVELENGTH_MAX - WAVELENGTH_MIN) / (CIE_COLOR_MATCHING_CURVES.length - 1);

    static {
        if (WAVELENGTH_STEP * (CIE_COLOR_MATCHING_CURVES.length - 1) != WAVELENGTH_MAX - WAVELENGTH_MIN)
            throw new RuntimeException("Internal error - spectrum static data is inconsistent!");
    }

    /**
     * Convert this curve to a tristimulus CIE XYZ color by integrating against
     * the CIE color matching functions.
     * 
     * @return XYZColor that represents this spectra
     */
    public final XYZColor toXYZ() {
        float X = 0, Y = 0, Z = 0;
        for (int i = 0, w = WAVELENGTH_MIN; i < CIE_COLOR_MATCHING_CURVES.length; i++, w += WAVELENGTH_STEP) {
            float s = sample(w);
            X += s * CIE_COLOR_MATCHING_CURVES[i][0];
            Y += s * CIE_COLOR_MATCHING_CURVES[i][1];
            Z += s * CIE_COLOR_MATCHING_CURVES[i][2];
        }
        return new XYZColor(X, Y, Z);
    }
}